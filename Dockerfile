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
RUN npm install --only=production

COPY server.js ./
COPY payBySquareGenerator.js ./
COPY frame.png ./

EXPOSE 3000

CMD ["node", "server.js"]