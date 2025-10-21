# PayBySquare API

Express aplikácia pre generovanie PayBySquare QR kódov podľa slovenského štandardu.

## Štruktúra projektu

```
.
├── server.js                 # Express server a endpointy
├── payBySquareGenerator.js   # Logika pre generovanie kódov a QR kódov
├── package.json              # Závislosti a konfigurácia
├── frame.png                 # Šablóna pre PAY by square rámček
└── README.md                 # Dokumentácia
```

## Inštalácia

```bash
npm install
```

**Dôležité:** Umiestni súbor `frame.png` do kořenového adresára projektu (vedľa `server.js`). Tento súbor sa použije ako šablóna pre PAY by square rámček okolo QR kódu.

## Nativné závislosti

Knižnica `canvas` vyžaduje nativné závislosti:

**Ubuntu/Debian:**
```bash
sudo apt-get install build-essential libcairo2-dev libpango1.0-dev libjpeg-dev libgif-dev librsvg2-dev
```

**MacOS:**
```bash
brew install pkg-config cairo pango libpng jpeg giflib librsvg
```

## Spustenie

```bash
npm start
```

Pre development s auto-reloadom:
```bash
npm run dev
```

Server beží na porte 3000 (alebo PORT z environment premennej).

## API Endpointy

### POST /pay-by-square/generate-qr

Vygeneruje QR kód a vráti ho ako PNG obrázok (binárne dáta).

**Request body (JSON):**
```json
{
  "amount": 123.45,
  "iban": "SK7700000000000000000000",
  "swift": "FIOZSKBAXXX",
  "beneficiaryName": "Meno Prijemcu",
  "currency": "EUR",
  "variableSymbol": "123456",
  "constantSymbol": "0308",
  "specificSymbol": "789",
  "note": "Platba za fakturu",
  "beneficiaryAddress1": "Hlavna 1",
  "beneficiaryAddress2": "Bratislava",
  "date": "2025-10-21",
  "withFrame": true,
  "qrSize": 300
}
```

**Povinné polia:**
- `amount` - suma platby (číslo)
- `iban` - IBAN účtu príjemcu

**Voliteľné polia:**
- `swift` - SWIFT kód banky (default: '')
- `currency` - mena (default: 'EUR')
- `date` - dátum platby vo formáte YYYY-MM-DD (default: dnes)
- `beneficiaryName` - meno príjemcu
- `variableSymbol` - variabilný symbol
- `constantSymbol` - konštantný symbol
- `specificSymbol` - špecifický symbol
- `note` - správa pre príjemcu
- `beneficiaryAddress1` - adresa príjemcu riadok 1
- `beneficiaryAddress2` - adresa príjemcu riadok 2
- `withFrame` - pridať "PAY by square" rámček z frame.png (default: true)
- `qrSize` - veľkosť QR kódu v px pred vložením do rámčeka (default: 300)

**Response:**
- Content-Type: `image/png`
- Body: binárne dáta PNG obrázka

**Príklad curl:**
```bash
# S rámčekom (default)
curl -X POST http://localhost:3000/pay-by-square/generate-qr \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "iban": "SK7700000000000000000000",
    "swift": "FIOZSKBAXXX",
    "beneficiaryName": "Test User",
    "variableSymbol": "12345"
  }' \
  --output qr-code.png

# Bez rámčeka
curl -X POST http://localhost:3000/pay-by-square/generate-qr \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "iban": "SK7700000000000000000000",
    "withFrame": false
  }' \
  --output qr-code-no-frame.png
```

### POST /pay-by-square/generate-code

Vygeneruje len PayBySquare kód (text) bez QR obrázka.

**Request:** Rovnaký ako `/pay-by-square/generate-qr`

**Response:**
```json
{
  "code": "0004G00006F071MBI3LRVO4PS..."
}
```

### GET /pay-by-square/version.txt

Vráti číslo verzie aplikácie z `package.json`.

**Response:**
- Content-Type: `text/plain`
- Body: číslo verzie (napr. `1.0.0`)

**Príklad curl:**
```bash
curl http://localhost:3000/pay-by-square/version.txt
# Output: 1.0.0
```

## Deployment

### Docker

Vytvor `Dockerfile`:

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY server.js ./

EXPOSE 3000

CMD ["node", "server.js"]
```

Build a spustenie:
```bash
docker build -t paybysquare-api .
docker run -p 3000:3000 paybysquare-api
```

### Render / Railway / Heroku

Aplikácia je pripravená na deployment na platformy ako Render, Railway alebo Heroku. Stačí nastaviť:
- Build command: `npm install`
- Start command: `npm start`

## Použitie ako Node.js modul

Môžeš použiť `payBySquareGenerator.js` aj priamo v inom projekte:

```javascript
const { 
  generatePayBySquareCode, 
  generatePayBySquareQR 
} = require('./payBySquareGenerator');

// Vygeneruj len kód
const code = await generatePayBySquareCode({
  amount: 100,
  iban: 'SK7700000000000000000000',
  beneficiaryName: 'Test User'
});

// Vygeneruj QR kód s rámčekom
const qrBuffer = await generatePayBySquareQR({
  amount: 100,
  iban: 'SK7700000000000000000000',
  beneficiaryName: 'Test User'
}, {
  withFrame: true,
  qrSize: 300
});

// Ulož do súboru
const fs = require('fs');
fs.writeFileSync('payment-qr.png', qrBuffer);
```

## API funkcie modulu

### `generatePayBySquareCode(paymentData)`
Vygeneruje PayBySquare kód (text string).

### `generateQRCodeImage(code, options)`
Vygeneruje QR kód obrázok z PayBySquare kódu.

### `addPayBySquareFrame(qrBuffer, framePath)`
Pridá rámček okolo QR kódu.

### `generatePayBySquareQR(paymentData, options)`
Kompletná funkcia - vygeneruje PayBySquare kód a vytvorí QR obrázok s rámčekom.

```javascript
// Fetch API
const response = await fetch('http://localhost:3000/pay-by-square/generate-qr', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        amount: 100.50,
        iban: 'SK7700000000000000000000',
        beneficiaryName: 'Obchod XYZ',
        variableSymbol: '2025001'
    })
});

const blob = await response.blob();
const imageUrl = URL.createObjectURL(blob);

// Zobrazenie v HTML
document.getElementById('qr-image').src = imageUrl;
```

## Poznámky

- Aplikácia používa LZMA kompresiu pre generovanie PayBySquare kódov
- QR kódy sú generované s error correction level M
- Default veľkosť QR kódu je 300x300 px (pred vložením do rámčeka)
- Súbor `frame.png` musí byť umiestnený v kořenovom adresári projektu
- QR kód sa automaticky vycentruje a škáluje na 70% veľkosti rámčeka
- Ak `frame.png` neexistuje a `withFrame=true`, vráti sa QR kód bez rámčeka