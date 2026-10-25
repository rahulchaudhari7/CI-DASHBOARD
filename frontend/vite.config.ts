import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: { port: 5173 },
  build: {
    rolldownOptions: {
      output: {
        codeSplitting: {
          groups: [
            { name: 'charts', test: /node_modules[\\/]recharts/, priority: 10 },
            { name: 'motion', test: /node_modules[\\/]framer-motion/, priority: 10 },
          ],
        },
      },
    },
  },
});
