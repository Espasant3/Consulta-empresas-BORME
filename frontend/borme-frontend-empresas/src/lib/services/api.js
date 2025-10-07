// Datos mock completos para desarrollo
const mockCompanies = [
    {
        id: 1,
        name: 'ARQTEC SOLUCIONES SL',
        creationDate: '2025-07-31',
        businessObject: '1. Intermediación de servicios en los campos de Arquitectura, Urbanismo e Ingeniería, con relación a la elaboración de proyectos, mediante contratación de técnicos competentes, adquisición, urbanización y venta de terrenos, la promoción y venta de inmuebles: la compra, venta, arriendo y subarriendo.',
        address: 'C/ CEDACEROS 4-10 BAJO A (ORIHUELA)',
        capital: '324.768,79',
        operationStartDate: '31.07.25',
        solePartnerDeclaration: 'Declaración de unipersonalidad',
        solePartner: 'MONTESINOS Y ESQUIVA SA',
        soleAdministrator: 'MONTESINOS VARO DIEGO',
        registryData: 'S 8, H A 199948, I/A 1 (25.08.25)',
        pdfUrl: '#'
    },
    {
        id: 2,
        name: 'NOBILE INSTALACIONES SL',
        creationDate: '2025-08-01',
        businessObject: 'Instalación de carpintería',
        address: 'C/ JAIME SEGARRA 61.49 IZ (ALICANTE)',
        capital: '3.000,00',
        operationStartDate: '01.08.25',
        solePartnerDeclaration: 'Declaración de unipersonalidad',
        solePartner: 'RODRIGUEZ CONTRERAS CARLOS HUMBERTO',
        soleAdministrator: 'RODRIGUEZ CONTRERAS CARLOS HUMBERTO',
        registryData: 'S 8, H A 199956, I/A 1 (25.08.25)',
        pdfUrl: '#'
    }
];

export const companyService = {
    async getCompanies(date = '') {
        console.log('Using mock data for companies');
        // Simular delay de red
        await new Promise(resolve => setTimeout(resolve, 500));

        if (date) {
            return mockCompanies.filter(company => company.creationDate === date);
        }
        return mockCompanies;
    },

    async getCompanyById(id) {
        console.log('Getting company by ID:', id);
        // Simular delay de red
        await new Promise(resolve => setTimeout(resolve, 300));

        const company = mockCompanies.find(company => company.id === parseInt(id));
        if (!company) {
            throw new Error('Company not found');
        }
        return company;
    },

    async searchCompanies(query) {
        await new Promise(resolve => setTimeout(resolve, 300));
        const lowerQuery = query.toLowerCase();
        return mockCompanies.filter(company =>
            company.name.toLowerCase().includes(lowerQuery) ||
            company.businessObject.toLowerCase().includes(lowerQuery) ||
            company.address.toLowerCase().includes(lowerQuery)
        );
    }
};