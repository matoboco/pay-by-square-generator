// payBySquareGenerator.js
const lzma = require('lzma-native');
const QRCode = require('qrcode');
const { crc32 } = require('crc');
const { createCanvas, loadImage } = require('canvas');

/**
 * Generate PayBySquare code from payment parameters
 */
async function generatePayBySquareCode({
                                           // Invoice identification
                                           invoiceId = '',

                                           // Payment options
                                           paymentOptions = ['paymentorder'], // ['paymentorder', 'standingorder', 'directdebit']

                                           // Basic payment info
                                           amount,
                                           iban,
                                           swift = '',
                                           date = null,
                                           paymentDueDate = null,
                                           beneficiaryName = '',
                                           currency = 'EUR',

                                           // Payment references
                                           variableSymbol = '',
                                           constantSymbol = '',
                                           specificSymbol = '',
                                           originatorsReferenceInformation = '',
                                           note = '',

                                           // Beneficiary address
                                           beneficiaryAddress1 = '',
                                           beneficiaryAddress2 = '',

                                           // Multiple bank accounts (array of {iban, swift})
                                           bankAccounts = null,

                                           // Standing order extension
                                           standingOrder = null, // {day, month, periodicity, lastDate}

                                           // Direct debit extension
                                           directDebit = null // {scheme, type, variableSymbol, specificSymbol, originatorsReferenceInformation, mandateId, creditorId, contractId, maxAmount, validTillDate}
                                       }) {
    // Use current date if not provided
    const paymentDate = date ? new Date(date) : new Date();
    const dateStr = paymentDate.toISOString().slice(0, 10).replace(/-/g, '');

    // Format payment due date if provided
    let dueDateStr = '';
    if (paymentDueDate) {
        const dueDate = new Date(paymentDueDate);
        dueDateStr = dueDate.toISOString().slice(0, 10).replace(/-/g, '');
    }

    // Convert payment options array to integer
    const paymentOptionsMap = {
        'paymentorder': 1,
        'standingorder': 2,
        'directdebit': 4
    };
    let paymentOptionsInt = 0;
    paymentOptions.forEach(opt => {
        if (paymentOptionsMap[opt]) {
            paymentOptionsInt += paymentOptionsMap[opt];
        }
    });

    // Prepare bank accounts - if not provided, use single IBAN/SWIFT
    let accounts = bankAccounts;
    if (!accounts || accounts.length === 0) {
        accounts = [{ iban, swift }];
    }

    // 1) Create the basic data structure
    const data = [
        invoiceId,
        '1', // Number of payments (currently supporting single payment)
        paymentOptionsInt.toString(),
        parseFloat(amount).toFixed(2),
        currency,
        dueDateStr || dateStr,
        variableSymbol,
        constantSymbol,
        specificSymbol,
        originatorsReferenceInformation,
        note,
        accounts.length.toString(), // Number of bank accounts
    ];

    // Add all bank accounts
    accounts.forEach(account => {
        data.push(account.iban || '');
        data.push(account.swift || '');
    });

    // Standing order extension
    if (standingOrder) {
        data.push('1'); // StandingOrderExt present
        data.push((standingOrder.day || '').toString());

        // Convert month array to integer if provided
        let monthInt = 0;
        if (standingOrder.month && Array.isArray(standingOrder.month)) {
            standingOrder.month.forEach(m => {
                monthInt += Math.pow(2, m - 1); // month 1 = 2^0, month 2 = 2^1, etc.
            });
        }
        data.push(monthInt.toString());

        data.push(standingOrder.periodicity || '');

        // Format last date
        let lastDateStr = '';
        if (standingOrder.lastDate) {
            const lastDate = new Date(standingOrder.lastDate);
            lastDateStr = lastDate.toISOString().slice(0, 10).replace(/-/g, '');
        }
        data.push(lastDateStr);
    } else {
        data.push('0'); // No standing order extension
    }

    // Direct debit extension
    if (directDebit) {
        data.push('1'); // DirectDebitExt present

        // Direct debit scheme: 0 = other, 1 = SEPA
        const schemeMap = { 'other': 0, 'sepa': 1 };
        data.push((schemeMap[directDebit.scheme?.toLowerCase()] || 0).toString());

        // Direct debit type: 0 = one-off, 1 = recurrent
        const typeMap = { 'oneoff': 0, 'one-off': 0, 'recurrent': 1 };
        data.push((typeMap[directDebit.type?.toLowerCase()] || 0).toString());

        data.push(directDebit.variableSymbol || '');
        data.push(directDebit.specificSymbol || '');
        data.push(directDebit.originatorsReferenceInformation || '');
        data.push(directDebit.mandateId || '');
        data.push(directDebit.creditorId || '');
        data.push(directDebit.contractId || '');

        // Max amount
        const maxAmountStr = directDebit.maxAmount ? parseFloat(directDebit.maxAmount).toFixed(2) : '';
        data.push(maxAmountStr);

        // Valid till date
        let validTillDateStr = '';
        if (directDebit.validTillDate) {
            const validDate = new Date(directDebit.validTillDate);
            validTillDateStr = validDate.toISOString().slice(0, 10).replace(/-/g, '');
        }
        data.push(validTillDateStr);
    } else {
        data.push('0'); // No direct debit extension
    }

    // Beneficiary information (added in version 1.1.0)
    data.push(beneficiaryName);
    data.push(beneficiaryAddress1);
    data.push(beneficiaryAddress2);

    const dataString = data.join('\t');

    // 2) Add CRC32 checksum
    const dataBuffer = Buffer.from(dataString, 'utf8');
    const checksum = crc32(dataBuffer);
    const checksumBuffer = Buffer.alloc(4);
    checksumBuffer.writeUInt32LE(checksum, 0);

    const total = Buffer.concat([checksumBuffer, dataBuffer]);

    // 3) Compress with LZMA
    const compressor = lzma.createStream('rawEncoder', {
        filters: [
            {
                id: lzma.FILTER_LZMA1,
                options: {
                    lc: 3,
                    lp: 0,
                    pb: 2,
                    dictSize: 128 * 1024
                }
            }
        ]
    });

    const compressed = await new Promise((resolve, reject) => {
        const chunks = [];
        compressor.on('data', chunk => chunks.push(chunk));
        compressor.on('end', () => resolve(Buffer.concat(chunks)));
        compressor.on('error', reject);
        compressor.write(total);
        compressor.end();
    });

    // 4) Prepend length
    const lengthBuffer = Buffer.alloc(4);
    lengthBuffer.writeUInt16LE(0, 0);
    lengthBuffer.writeUInt16LE(total.length, 2);
    const compressedWithLength = Buffer.concat([lengthBuffer, compressed]);

    // 5) Convert to binary string
    let binary = '';
    for (const byte of compressedWithLength) {
        binary += byte.toString(2).padStart(8, '0');
    }

    // 6) Pad with zeros to multiple of 5
    const remainder = binary.length % 5;
    if (remainder) {
        binary += '0'.repeat(5 - remainder);
    }

    // 7) Convert quintets to characters
    const subst = '0123456789ABCDEFGHIJKLMNOPQRSTUV';
    let result = '';
    for (let i = 0; i < binary.length; i += 5) {
        const quintet = binary.substr(i, 5);
        result += subst[parseInt(quintet, 2)];
    }

    return result;
}

/**
 * Generate QR code image from PayBySquare code
 */
async function generateQRCodeImage(code, options = {}) {
    const {
        width = 300,
        errorCorrectionLevel = 'M',
        margin = 1
    } = options;

    return await QRCode.toBuffer(code, {
        errorCorrectionLevel,
        type: 'png',
        width,
        margin
    });
}

/**
 * Add PAY by square frame around QR code using frame.png template
 */
async function addPayBySquareFrame(qrBuffer, framePath = './frame.png') {
    try {
        // Load frame template
        const frameImage = await loadImage(framePath);

        // Load QR code image
        const qrImage = await loadImage(qrBuffer);

        // Create canvas with frame dimensions
        const canvas = createCanvas(frameImage.width, frameImage.height);
        const ctx = canvas.getContext('2d');

        // Draw frame as background
        ctx.drawImage(frameImage, 0, 0);

        // Calculate QR code position to center it
        // 85% of frame size for better fit
        const qrSize = Math.min(frameImage.width, frameImage.height) * 0.85;
        const qrX = (frameImage.width - qrSize) / 2;

        // Draw QR code - using qrX for both X and Y to center it perfectly
        ctx.drawImage(qrImage, qrX, qrX, qrSize, qrSize);

        return canvas.toBuffer('image/png');
    } catch (error) {
        console.error('Error loading frame.png:', error);
        // If frame.png doesn't exist, return QR code without frame
        return qrBuffer;
    }
}

/**
 * Generate complete QR code with optional frame
 */
async function generatePayBySquareQR(paymentData, options = {}) {
    const {
        withFrame = true,
        qrSize = 300,
        framePath = './frame.png'
    } = options;

    // Generate PayBySquare code
    const code = await generatePayBySquareCode(paymentData);

    // Generate QR code image
    let qrBuffer = await generateQRCodeImage(code, { width: qrSize });

    // Add frame if requested
    if (withFrame) {
        qrBuffer = await addPayBySquareFrame(qrBuffer, framePath);
    }

    return qrBuffer;
}

module.exports = {
    generatePayBySquareCode,
    generateQRCodeImage,
    addPayBySquareFrame,
    generatePayBySquareQR
};