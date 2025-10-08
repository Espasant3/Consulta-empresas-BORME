import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import path from 'path'

export default defineConfig({
    plugins: [svelte()],
    resolve: {
        alias: {
            $lib: path.resolve('./src/lib')
        }
    },
    server: {
        host: true,
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            }
        }
    },
    // Configuración para producción
    build: {
        outDir: 'dist',
        sourcemap: true
    },
    // Variables de entorno
    define: {
        'import.meta.env.VITE_API_BASE': JSON.stringify(process.env.VITE_API_BASE)
    }
})