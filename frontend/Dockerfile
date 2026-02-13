FROM node:22-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# Expose port Vite uses
EXPOSE 5173

# Start the dev server (with --host to allow external access)
CMD ["npm", "run", "dev", "--", "--host"]