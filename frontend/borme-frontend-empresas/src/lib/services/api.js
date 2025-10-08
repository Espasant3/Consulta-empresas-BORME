const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';

// Helper para hacer fetch con manejo de errores mejorado
async function fetchAPI(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;

    console.log(`Making API call to: ${url}`);

    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers,
            },
            ...options,
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText || response.statusText}`);
        }

        const data = await response.json();
        console.log(`API response from ${url}:`, data);
        return data;
    } catch (error) {
        console.error(`API call failed for ${url}:`, error);
        throw error;
    }
}

// Función para normalizar la respuesta de empresas (siempre devuelve array)
function normalizeCompaniesResponse(data) {
    if (Array.isArray(data)) {
        return data;
    }

    // Si es un objeto, buscar posibles propiedades que contengan el array
    if (data && typeof data === 'object') {
        if (Array.isArray(data.empresas)) return data.empresas;
        if (Array.isArray(data.data)) return data.data;
        if (Array.isArray(data.content)) return data.content;
        if (Array.isArray(data.result)) return data.result;

        // Si tiene una única propiedad que es array, devolverla
        const arrayProps = Object.values(data).filter(Array.isArray);
        if (arrayProps.length === 1) return arrayProps[0];
    }

    console.warn('Could not normalize companies response, returning empty array:', data);
    return [];
}

export const companyService = {
    // Obtener todas las constituciones almacenadas
    async getAllCompanies() {
        const data = await fetchAPI('/empresas');
        return normalizeCompaniesResponse(data);
    },

    // Consultar constituciones por fecha específica
    async getCompaniesByDate(date) {
        if (!date) throw new Error('La fecha es requerida');
        const data = await fetchAPI(`/empresas/fecha/${date}`);
        return normalizeCompaniesResponse(data);
    },

    // Obtener detalles de una constitución específica
    async getCompanyDetail(numeroAsiento, fechaConstitucion) {
        if (!numeroAsiento || !fechaConstitucion) {
            throw new Error('Número de asiento y fecha de constitución son requeridos');
        }
        return await fetchAPI(`/empresas/${numeroAsiento}/${fechaConstitucion}`);
    },

    // Buscar constituciones por nombre
    async searchCompaniesByName(query) {
        if (!query) return this.getAllCompanies();
        const data = await fetchAPI(`/empresas/buscar?nombre=${encodeURIComponent(query)}`);
        return normalizeCompaniesResponse(data);
    },

    // Obtener estadísticas
    async getStatistics() {
        return await fetchAPI('/empresas/estadisticas');
    },

    // Verificar si existe un PDF
    async checkPdfExists(fecha, nombreArchivo) {
        if (!fecha || !nombreArchivo) return false;

        try {
            const response = await fetch(`${API_BASE}/pdfs/${fecha}/${nombreArchivo}/existe`);
            return response.ok;
        } catch (error) {
            console.error('Error checking PDF existence:', error);
            return false;
        }
    },

    // Obtener URL del PDF para visualización
    getPdfUrl(company) {
        if (!company) return null;

        // Prioridad 1: Si la empresa ya tiene una URL de PDF completa
        if (company.urlPDF) {
            return company.urlPDF.startsWith('http')
                ? company.urlPDF
                : `${company.urlPDF.startsWith('/') ? '' : '/'}${company.urlPDF}`;
        }

        // Prioridad 2: Construir la URL usando fechaPDF y nombreArchivoPDF
        if (company.fechaPDF && company.nombreArchivoPDF) {
            const formattedFecha = company.fechaPDF.replace(/-/g, '/');
            return `${API_BASE}/pdfs/${formattedFecha}/${company.nombreArchivoPDF}`;
        }

        console.warn('No PDF URL available for company:', company);
        return null;
    },

    // Consultar BORME para una fecha específica (HTML crudo)
    async getBormeRaw(fecha) {
        if (!fecha) throw new Error('La fecha es requerida');
        return await fetchAPI(`/borme/${fecha}`);
    },

    // Validar si una fecha es válida para consultar el BORME
    async validateBormeDate(fecha) {
        if (!fecha) throw new Error('La fecha es requerida');
        return await fetchAPI(`/borme/validar/${fecha}`);
    }
};

// Función para generar ID único
export function generateCompanyId(company) {
    return `${company.fechaConstitucion}-${company.numeroAsiento}`;
}