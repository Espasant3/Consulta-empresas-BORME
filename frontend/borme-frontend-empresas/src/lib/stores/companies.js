import { writable, derived } from 'svelte/store';
import { companyService } from '$lib/services/api';

// Inicializar stores
export const companies = writable([]);
export const filters = writable({
    numeroAsiento: '',
    fechaConstitucion: '',
    nombreEmpresa: '',
    objetoSocial: '',
    domicilio: '',
    capital: ''
});
export const selectedCompany = writable(null);
export const loading = writable(false);
export const lastSearchedDate = writable('');
export const expandedCompanyId = writable(null);
export const statistics = writable(null);
export const initialLoadComplete = writable(false);
export const recentSearchResults = writable([]);

// Añade esta función para limpiar resultados recientes
export function clearRecentResults() {
    recentSearchResults.set([]);
}

// Función para generar ID
export function generateCompanyId(company) {
    return `${company.fechaConstitucion}-${company.numeroAsiento}`;
}

// Companies filtradas
export const filteredCompanies = derived(
    [companies, filters],
    ([$companies, $filters]) => {
        if (!$companies || $companies.length === 0) return [];

        return $companies.filter(company => {
            return Object.entries($filters).every(([key, value]) => {
                if (!value) return true;
                const companyValue = company[key] || '';
                return companyValue.toString().toLowerCase().includes(value.toLowerCase());
            });
        });
    }
);

// Función para cargar todas las empresas (pero no bloquear la UI)
export async function loadAllCompanies() {
    loading.set(true);
    try {
        const allCompanies = await companyService.getAllCompanies();
        companies.set(allCompanies);
        initialLoadComplete.set(true);
        return allCompanies;
    } catch (error) {
        console.error('Error loading all companies:', error);
        // No lanzamos error para no bloquear la UI
        return [];
    } finally {
        loading.set(false);
    }
}

// Función para consultar empresas por fecha
export async function searchCompaniesByDate(date) {
    if (!date) {
        throw new Error('La fecha es requerida');
    }

    loading.set(true);
    try {
        const newCompanies = await companyService.getCompaniesByDate(date);

        console.log('Companies received from API:', newCompanies);

        // Verificar que newCompanies es un array
        if (!Array.isArray(newCompanies)) {
            console.error('Expected array but got:', typeof newCompanies, newCompanies);
            throw new Error('La respuesta del servidor no es válida');
        }

        // Guardar los resultados recientes
        recentSearchResults.set(newCompanies);

        // Recargar todas las empresas para actualizar la tabla general
        const allCompanies = await companyService.getAllCompanies();
        companies.set(allCompanies);

        lastSearchedDate.set(date);
        return newCompanies;
    } catch (error) {
        console.error('Error searching companies by date:', error);
        throw error;
    } finally {
        loading.set(false);
    }
}

// Función para cargar detalles de una empresa
export async function loadCompanyDetail(companyId) {
    try {
        const [fechaConstitucion, numeroAsiento] = companyId.split('-');
        const companyDetail = await companyService.getCompanyDetail(numeroAsiento, fechaConstitucion);
        selectedCompany.set(companyDetail);
        return companyDetail;
    } catch (error) {
        console.error('Error loading company detail:', error);
        throw error;
    }
}

// Función para cargar estadísticas
export async function loadStatistics() {
    try {
        const stats = await companyService.getStatistics();
        statistics.set(stats);
        return stats;
    } catch (error) {
        console.error('Error loading statistics:', error);
        // No lanzamos error para no bloquear la UI
        return null;
    }
}

// Función para actualizar un filtro específico
export function updateFilter(key, value) {
    filters.update(current => {
        return { ...current, [key]: value };
    });
}

// Función para limpiar todos los filtros
export function clearFilters() {
    filters.set({
        numeroAsiento: '',
        fechaConstitucion: '',
        nombreEmpresa: '',
        objetoSocial: '',
        domicilio: '',
        capital: ''
    });
}

// Función para expandir/contraer empresa
export function toggleCompanyExpansion(companyId) {
    expandedCompanyId.update(current => current === companyId ? null : companyId);
}

// Función para limpiar todas las empresas
export function clearAllCompanies() {
    companies.set([]);
    initialLoadComplete.set(true);
}