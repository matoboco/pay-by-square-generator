// payBySquareGenerator.js
const lzma = require('lzma-native');
const QRCode = require('qrcode');
const { crc32 } = require('crc');
const { createCanvas, loadImage } = require('canvas');

/**
 * Generate PayBySquare code from payment parameters
 */
async function generatePayBySquareCode({
                                           amount,
                                           iban,
                                           swift = '',
                                           date = null,
                                           beneficiaryName = '',
                                           currency = 'EUR',
                                           variableSymbol = '',
                                           constantSymbol = '',
                                           specificSymbol = '',
                                           note = '',
                                           beneficiaryAddress1 = '',
                                           beneficiaryAddress2 = ''
                                       }) {
    // Use current date if not provided
    const paymentDate = date ? new Date(date) : new Date();
    const dateStr = paymentDate.toISOString().slice(0, 10).replace(/-/g, '');

    // 1) Create the basic data structure
    const data = [
        '',
        '1',  // payment
        '1',  // simple payment
        parseFloat(amount).toFixed(2),
        currency,
        dateStr,
        variableSymbol,
        constantSymbol,
        specificSymbol,
        '',  // previous 3 entries in SEPA format
        note,
        '1',  // to an account
        iban,
        swift,
        '0',  // not recurring
        '0',  // not 'inkaso'
        beneficiaryName,
        beneficiaryAddress1,
        beneficiaryAddress2
    ].join('\t');

    // 2) Add CRC32 checksum
    const dataBuffer = Buffer.from(data, 'utf8');
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
        // Assuming the frame has some padding/border, adjust these values if needed
        const qrSize = Math.min(frameImage.width, frameImage.height) * 0.85; // 85% of frame size
        const qrX = (frameImage.width - qrSize) / 2;
        const qrY = (frameImage.height - qrSize) / 2 - 25; // Shifted 30px higher

        // Draw QR code in center
        ctx.drawImage(qrImage, qrX, qrY, qrSize, qrSize);

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