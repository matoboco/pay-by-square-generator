// server.js
const express = require('express');
const { version } = require('./package.json');
const { generatePayBySquareCode, generatePayBySquareQR } = require('./payBySquareGenerator');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const path = require('path');

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Load OpenAPI specification
const openApiDocument = YAML.load(path.join(__dirname, 'openapi.yaml'));

// Swagger UI
app.use('/pay-by-square-generator/docs', swaggerUi.serve, swaggerUi.setup(openApiDocument, {
  customCss: '.swagger-ui .topbar { display: none }',
  customSiteTitle: 'PayBySquare API Documentation'
}));

// Redirect root to documentation
app.get('/', (req, res) => {
  res.redirect('/pay-by-square-generator/docs');
});

/**
 * POST /pay-by-square-generator/generate-qr
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
 * POST /pay-by-square-generator/generate-code
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
 * GET /pay-by-square-generator/version.txt
 * Returns version number from package.json
 */
app.get('/pay-by-square-generator/version.txt', (req, res) => {
  res.set('Content-Type', 'text/plain');
  res.send(version);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`PayBySquare API running on port ${PORT}`);
  console.log(`Documentation available at http://localhost:${PORT}/pay-by-square-generator/docs`);
});