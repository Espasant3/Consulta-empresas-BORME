<script>
    import { onMount } from 'svelte';
    import { selectedCompany } from '$lib/stores/companies';
    import { companyService } from '$lib/services/api';
    import { useParams } from 'svelte-routing';

    let loading = true;
    let error = null;
    const params = useParams();

    onMount(async () => {
        try {
            const companyId = params.id;
            console.log('Loading company with ID:', companyId);
            const company = await companyService.getCompanyById(companyId);
            selectedCompany.set(company);
        } catch (err) {
            error = 'Error al cargar los datos de la empresa';
            console.error('Error loading company:', err);
        } finally {
            loading = false;
        }
    });
</script>

<div class="detail-container">
    {#if loading}
        <div class="loading">Cargando...</div>
    {:else if error}
        <div class="error">{error}</div>
    {:else}
        {#if $selectedCompany}
            <div class="company-detail">
                <h1>{$selectedCompany.name}</h1>

                <div class="detail-grid">
                    <div class="detail-item">
                        <span class="label">Fecha de Constitución:</span>
                        <span class="value">{$selectedCompany.creationDate}</span>
                    </div>

                    <div class="detail-item">
                        <span class="label">Inicio de Operaciones:</span>
                        <span class="value">{$selectedCompany.operationStartDate}</span>
                    </div>

                    <div class="detail-item full-width">
                        <span class="label">Objeto Social:</span>
                        <span class="value">{$selectedCompany.businessObject}</span>
                    </div>

                    <div class="detail-item full-width">
                        <span class="label">Domicilio:</span>
                        <span class="value">{$selectedCompany.address}</span>
                    </div>

                    <div class="detail-item">
                        <span class="label">Capital:</span>
                        <span class="value">{$selectedCompany.capital} €</span>
                    </div>

                    {#if $selectedCompany.solePartnerDeclaration}
                        <div class="detail-item">
                            <span class="label">Declaración Unipersonalidad:</span>
                            <span class="value">{$selectedCompany.solePartnerDeclaration}</span>
                        </div>
                    {/if}

                    {#if $selectedCompany.solePartner}
                        <div class="detail-item">
                            <span class="label">Socio Único:</span>
                            <span class="value">{$selectedCompany.solePartner}</span>
                        </div>
                    {/if}

                    {#if $selectedCompany.soleAdministrator}
                        <div class="detail-item">
                            <span class="label">Administrador Único:</span>
                            <span class="value">{$selectedCompany.soleAdministrator}</span>
                        </div>
                    {/if}

                    <div class="detail-item">
                        <span class="label">Datos Registrales:</span>
                        <span class="value">{$selectedCompany.registryData}</span>
                    </div>
                </div>

                {#if $selectedCompany.pdfUrl}
                    <div class="pdf-section">
                        <h3>Documento Original</h3>
                        <a
                                href={$selectedCompany.pdfUrl}
                                target="_blank"
                                class="pdf-link"
                        >
                            📄 Ver PDF original
                        </a>
                    </div>
                {/if}
            </div>
        {:else}
            <div class="error">Empresa no encontrada</div>
        {/if}
    {/if}
</div>

<style>
    .detail-container {
        max-width: 800px;
        margin: 0 auto;
        padding: 2rem;
    }

    .company-detail h1 {
        color: #1e293b;
        margin-bottom: 2rem;
        border-bottom: 2px solid #e2e8f0;
        padding-bottom: 0.5rem;
    }

    .detail-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.5rem;
        margin-bottom: 2rem;
    }

    .detail-item {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .detail-item.full-width {
        grid-column: 1 / -1;
    }

    .label {
        font-weight: 600;
        color: #475569;
        font-size: 0.875rem;
    }

    .value {
        color: #1e293b;
    }

    .pdf-section {
        border-top: 1px solid #e2e8f0;
        padding-top: 1.5rem;
    }

    .pdf-link {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.5rem;
        background-color: #3b82f6;
        color: white;
        text-decoration: none;
        border-radius: 0.375rem;
        font-weight: 500;
    }

    .pdf-link:hover {
        background-color: #2563eb;
    }

    .loading, .error {
        text-align: center;
        padding: 2rem;
        font-size: 1.125rem;
    }

    .error {
        color: #dc2626;
    }
</style>