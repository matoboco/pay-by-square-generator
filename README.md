# PayBySquare API

Express aplikácia pre generovanie PayBySquare QR kódov podľa slovenského štandardu.

## Štruktúra projektu

```
.
├── server.js                 # Express server a endpointy
├── payBySquareGenerator.js   # Logika pre generovanie kódov a QR kódov
├── openapi.yaml              # OpenAPI 3.0 špecifikácia API
├── package.json              # Závislosti a konfigurácia
├── frame.png                 # Šablóna pre PAY by square rámček
└── README.md                 # Dokumentácia
```

## OpenAPI Špecifikácia

API je plne zdokumentované pomocou OpenAPI 3.0 štandardu. Schému nájdeš v súbore `openapi.yaml`.

**Prístup k dokumentácii:**
- **Swagger UI** (interaktívna): `http://localhost:3000/pay-by-square-generator/docs`
- **OpenAPI súbor**: `openapi.yaml` (na generovanie klientov)

**Použitie OpenAPI schémy:**
- **Prehliadaj a testuj API** priamo vo Swagger UI
- Importuj do **Postman** alebo **Insomnia** pre testovanie
- Vygeneruj klientske knižnice pomocou **OpenAPI Generator**

**Generovanie klientov:**
```bash
# TypeScript/JavaScript client
npx @openapitools/openapi-generator-cli generate \
  -i openapi.yaml \
  -g typescript-axios \
  -o ./client

# Python client
npx @openapitools/openapi-generator-cli generate \
  -i openapi.yaml \
  -g python \
  -o ./python-client

# PHP client
npx @openapitools/openapi-generator-cli generate \
  -i openapi.yaml \
  -g php \
  -o ./php-client
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

**Interaktívna dokumentácia:**
Po spustení servera otvor prehliadač na:
- **Swagger UI:** `http://localhost:3000/pay-by-square-generator/docs`
- **Root URL:** `http://localhost:3000/` (presmeruje na dokumentáciu)

Swagger UI obsahuje:
- Interaktívne testovanie všetkých endpointov
- Kompletné schémy a príklady
- Možnosť "Try it out" priamo z prehliadača

## API Endpointy

### POST /pay-by-square-generator/generate-qr

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
  "paymentDueDate": "2025-11-21",
  "invoiceId": "INV-2025-001",
  "withFrame": true,
  "qrSize": 300
}
```

**Pokročilé použitie - viacero účtov:**
```json
{
  "amount": 100.00,
  "bankAccounts": [
    {
      "iban": "SK7700000000000000000000",
      "swift": "FIOZSKBAXXX"
    },
    {
      "iban": "SK8811000000001234567890",
      "swift": "TATRSKBXXXX"
    }
  ],
  "beneficiaryName": "Firma s.r.o.",
  "variableSymbol": "123456"
}
```

**Pokročilé použitie - trvalý príkaz:**
```json
{
  "amount": 50.00,
  "iban": "SK7700000000000000000000",
  "paymentOptions": ["paymentorder", "standingorder"],
  "standingOrder": {
    "day": 15,
    "month": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
    "periodicity": "m",
    "lastDate": "2026-12-31"
  }
}
```

**Pokročilé použitie - inkaso:**
```json
{
  "amount": 100.00,
  "iban": "SK7700000000000000000000",
  "paymentOptions": ["paymentorder", "directdebit"],
  "directDebit": {
    "scheme": "sepa",
    "type": "recurrent",
    "mandateId": "MANDATE-123",
    "creditorId": "SK12ZZZ00000000123",
    "maxAmount": 150.00,
    "validTillDate": "2026-12-31"
  }
}
```

**Povinné polia:**
- `amount` - suma platby (číslo)
- `iban` - IBAN účtu príjemcu

**Povinné polia:**
- `amount` - suma platby (číslo)
- `iban` - IBAN účtu príjemcu (alebo použiť `bankAccounts` array)

**Základné voliteľné polia:**
- `swift` - SWIFT kód banky (default: '')
- `currency` - mena (default: 'EUR')
- `date` - dátum platby vo formáte YYYY-MM-DD (default: dnes)
- `paymentDueDate` - dátum splatnosti vo formáte YYYY-MM-DD
- `invoiceId` - identifikátor faktúry (max 10 znakov)
- `beneficiaryName` - meno príjemcu (max 70 znakov)
- `variableSymbol` - variabilný symbol (max 10 znakov)
- `constantSymbol` - konštantný symbol (max 4 znaky)
- `specificSymbol` - špecifický symbol (max 10 znakov)
- `originatorsReferenceInformation` - SEPA referencia (max 35 znakov, alternatíva k VS/KS/SS)
- `note` - správa pre príjemcu (max 140 znakov)
- `beneficiaryAddress1` - adresa príjemcu riadok 1 (max 70 znakov)
- `beneficiaryAddress2` - adresa príjemcu riadok 2 (max 70 znakov)
- `withFrame` - pridať "PAY by square" rámček z frame.png (default: true)
- `qrSize` - veľkosť QR kódu v px pred vložením do rámčeka (default: 300)

**Pokročilé polia:**

**`paymentOptions`** - array typov platby (default: ['paymentorder'])
- Možné hodnoty: 'paymentorder', 'standingorder', 'directdebit'

**`bankAccounts`** - array bankových účtov (max 6 účtov)
```json
[
  {"iban": "SK...", "swift": "..."},
  {"iban": "SK...", "swift": "..."}
]
```
Prvý účet je predvolený. Ak nie je zadané, použije sa `iban` a `swift`.

**`standingOrder`** - nastavenia pre trvalý príkaz
- `day` - deň platby (1-31 pre deň v mesiaci, 1-7 pre deň v týždni)
- `month` - array mesiacov [1-12] kedy sa má platiť
- `periodicity` - periodicita: 'd' (daily), 'w' (weekly), 'b' (biweekly), 'm' (monthly), 'B' (bimonthly), 'q' (quarterly), 's' (semiannually), 'a' (annually)
- `lastDate` - dátum poslednej platby vo formáte YYYY-MM-DD

**`directDebit`** - nastavenia pre inkaso
- `scheme` - 'sepa' alebo 'other' (default: 'other')
- `type` - 'oneoff' alebo 'recurrent'
- `variableSymbol` - VS pre inkaso (max 10 znakov)
- `specificSymbol` - ŠS pre inkaso (max 10 znakov)
- `originatorsReferenceInformation` - SEPA referencia (max 35 znakov)
- `mandateId` - ID mandátu pre SEPA (max 35 znakov)
- `creditorId` - ID veriteľa pre SEPA (max 35 znakov)
- `contractId` - ID zmluvy pre SEPA (max 35 znakov)
- `maxAmount` - maximálna suma inkasa (číslo)
- `validTillDate` - dátum platnosti inkasa vo formáte YYYY-MM-DD

**Response:**
- Content-Type: `image/png`
- Body: binárne dáta PNG obrázka

**Príklad curl:**
```bash
# S rámčekom (default)
curl -X POST http://localhost:3000/pay-by-square-generator/generate-qr \
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
curl -X POST http://localhost:3000/pay-by-square-generator/generate-qr \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "iban": "SK7700000000000000000000",
    "withFrame": false
  }' \
  --output qr-code-no-frame.png
```

### POST /pay-by-square-generator/generate-code

Vygeneruje len PayBySquare kód (text) bez QR obrázka.

**Request:** Rovnaký ako `/pay-by-square-generator/generate-qr`

**Response:**
```json
{
  "code": "0004G00006F071MBI3LRVO4PS..."
}
```

### GET /pay-by-square-generator/version.txt

Vráti číslo verzie aplikácie z `package.json`.

**Response:**
- Content-Type: `text/plain`
- Body: číslo verzie (napr. `1.0.0`)

**Príklad curl:**
```bash
curl http://localhost:3000/pay-by-square-generator/version.txt
# Output: 1.0.0
```

## Deployment

### Docker

Vytvor `Dockerfile`:

```dockerfile
FROM node:18-alpine

# Install native dependencies for canvas
RUN apk add --no-cache \
    build-base \
    cairo-dev \
    jpeg-dev \
    pango-dev \
    giflib-dev \
    pixman-dev

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY server.js ./
COPY payBySquareGenerator.js ./
COPY openapi.yaml ./
COPY frame.png ./

EXPOSE 3000

CMD ["node", "server.js"]
```

Build a spustenie:
```bash
docker build -t paybysquare-api .
docker run -p 3000:3000 paybysquare-api
```

**Poznámka:** Alpine image je menší, ale vyžaduje nativné balíčky. Ak chceš ešte menší image, môžeš použiť multi-stage build.

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

// Jednoduchá platba
const code = await generatePayBySquareCode({
  amount: 100,
  iban: 'SK7700000000000000000000',
  beneficiaryName: 'Test User',
  invoiceId: 'INV-001',
  paymentDueDate: '2025-12-31'
});

// Platba s viacerými účtami
const qrBuffer = await generatePayBySquareQR({
  amount: 100,
  bankAccounts: [
    { iban: 'SK7700000000000000000000', swift: 'FIOZSKBAXXX' },
    { iban: 'SK8811000000001234567890', swift: 'TATRSKBXXXX' }
  ],
  beneficiaryName: 'Firma s.r.o.',
  variableSymbol: '123456'
}, {
  withFrame: true,
  qrSize: 300
});

// Trvalý príkaz - mesačná platba každý 15. deň
const standingOrderQr = await generatePayBySquareQR({
  amount: 50,
  iban: 'SK7700000000000000000000',
  paymentOptions: ['paymentorder', 'standingorder'],
  standingOrder: {
    day: 15,
    month: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12], // všetky mesiace
    periodicity: 'm', // monthly
    lastDate: '2026-12-31'
  }
});

// SEPA inkaso
const directDebitQr = await generatePayBySquareQR({
  amount: 100,
  iban: 'SK7700000000000000000000',
  paymentOptions: ['paymentorder', 'directdebit'],
  directDebit: {
    scheme: 'sepa',
    type: 'recurrent',
    mandateId: 'MANDATE-123',
    creditorId: 'SK12ZZZ00000000123',
    maxAmount: 150,
    validTillDate: '2026-12-31'
  }
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