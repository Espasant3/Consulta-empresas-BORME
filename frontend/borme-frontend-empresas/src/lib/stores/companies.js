import { writable, derived } from 'svelte/store';

export const companies = writable([]);
export const searchTerm = writable('');
export const selectedDate = writable('');
export const selectedCompany = writable(null);

export const filteredCompanies = derived(
    [companies, searchTerm, selectedDate],
    ([$companies, $searchTerm, $selectedDate]) => {
        return $companies.filter(company => {
            const matchesSearch = !$searchTerm ||
                company.name?.toLowerCase().includes($searchTerm.toLowerCase()) ||
                company.businessObject?.toLowerCase().includes($searchTerm.toLowerCase()) ||
                company.address?.toLowerCase().includes($searchTerm.toLowerCase());

            const matchesDate = !$selectedDate || company.creationDate === $selectedDate;

            return matchesSearch && matchesDate;
        });
    }
);