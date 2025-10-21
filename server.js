// server.js
const express = require('express');
const { version } = require('./package.json');
const { generatePayBySquareCode, generatePayBySquareQR } = require('./payBySquareGenerator');

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

/**
 * POST /pay-by-square/generate-qr
 * Generate QR code from payment parameters
 * Returns PNG image as binary data
 */
app.post('/pay-by-square-generator/generate-qr', async (req, res) => {
  try {
    const {
      amount,
      iban,
      withFrame = true,
      qrSize = 300
    } = req.body;

    // Validate required fields
    if (!amount || !iban) {
      return res.status(400).json({
        error: 'Missing required fields: amount and iban are mandatory'
      });
    }

    // Generate QR code with frame
    const qrBuffer = await generatePayBySquareQR(req.body, {
      withFrame,
      qrSize
    });

    // Return binary image data
    res.set('Content-Type', 'image/png');
    res.send(qrBuffer);

  } catch (error) {
    console.error('Error generating QR code:', error);
    res.status(500).json({
      error: 'Failed to generate QR code',
      message: error.message
    });
  }
});

/**
 * POST /pay-by-square/generate-code
 * Generate only PayBySquare code (text) without QR image
 */
app.post('/pay-by-square-generator/generate-code', async (req, res) => {
  try {
    const { amount, iban } = req.body;

    if (!amount || !iban) {
      return res.status(400).json({
        error: 'Missing required fields: amount and iban are mandatory'
      });
    }

    const code = await generatePayBySquareCode(req.body);

    res.json({ code });

  } catch (error) {
    console.error('Error generating code:', error);
    res.status(500).json({
      error: 'Failed to generate code',
      message: error.message
    });
  }
});

/**
 * GET /pay-by-square/version.txt
 * Returns version number from package.json
 */
app.get('/pay-by-square-generator/version.txt', (req, res) => {
  res.set('Content-Type', 'text/plain');
  res.send(version);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`PayBySquare API running on port ${PORT}`);
});