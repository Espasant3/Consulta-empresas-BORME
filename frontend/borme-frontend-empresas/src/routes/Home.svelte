<script>
    import CompanyTable from '$lib/components/CompanyTable.svelte';
    import SearchFilters from '$lib/components/SearchFilters.svelte';
    import { onMount } from 'svelte';
    import { companies } from '$lib/stores/companies';
    import { companyService } from '$lib/services/api';

    let loading = true;
    let error = null;

    onMount(async () => {
        try {
            console.log('Loading companies...');
            const companiesData = await companyService.getCompanies();
            console.log('Loaded companies:', companiesData);
            companies.set(companiesData);
        } catch (err) {
            console.error('Error loading companies:', err);
            error = 'Error al cargar las empresas: ' + err.message;
        } finally {
            loading = false;
        }
    });

    function handleCompanySelected(event) {
        dispatch('companySelected', event.detail);
    }
</script>

<div class="home">
    <div class="home-header">
        <h1>Constituciones de Empresas - BORME</h1>
    </div>

    {#if loading}
        <div class="loading">Cargando empresas...</div>
    {:else if error}
        <div class="error">{error}</div>
    {:else}
        <div class="home-content">
            <SearchFilters />
            <CompanyTable on:companySelected={handleCompanySelected} />
        </div>
    {/if}
</div>

<style>
    .home {
        flex: 1;
        display: flex;
        flex-direction: column;
        width: 100%;
    }

    .home-header {
        background: white;
        padding: 2rem;
        border-bottom: 1px solid #e2e8f0;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
    }

    h1 {
        color: #1e293b;
        margin: 0;
        font-size: 2rem;
        text-align: center;
    }

    .home-content {
        flex: 1;
        padding: 2rem;
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .loading {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2rem;
        color: #64748b;
        font-size: 1.125rem;
    }

    .error {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2rem;
        color: #dc2626;
        background: #fef2f2;
        border: 1px solid #fecaca;
        border-radius: 0.5rem;
        margin: 2rem;
        text-align: center;
    }
</style>