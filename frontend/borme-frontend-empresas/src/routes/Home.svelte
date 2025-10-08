<script>
    import CompanyTable from '$lib/components/CompanyTable.svelte';
    import DateSearch from '$lib/components/DateSearch.svelte';
    import RecentResultsTable from '$lib/components/RecentResultsTable.svelte'; // Nueva importación
    import { onMount } from 'svelte';
    import {
        companies,
        statistics,
        loadAllCompanies,
        loadStatistics,
        initialLoadComplete
    } from '$lib/stores/companies';

    let error = null;

    onMount(async () => {
        try {
            // Cargar en segundo plano sin bloquear la UI
            Promise.all([
                loadAllCompanies(),
                loadStatistics()
            ]).catch(err => {
                console.error('Error in background loading:', err);
            });
        } catch (err) {
            console.error('Error in onMount:', err);
        }
    });
</script>

<div class="home">
    <div class="home-header">
        <h1>Sistema de Consulta de Constituciones Empresariales - BORME</h1>
        <p class="subtitle">Consulta y gestiona las constituciones de empresas publicadas en el BORME</p>

        {#if $statistics}
            <div class="stats-bar">
                <div class="stat-item">
                    <span class="stat-number">{$statistics.totalEmpresas || 0}</span>
                    <span class="stat-label">Total Constituciones</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{$statistics.totalFechas || 0}</span>
                    <span class="stat-label">Fechas Consultadas</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{$statistics.ultimaActualizacion || 'N/A'}</span>
                    <span class="stat-label">Última Actualización</span>
                </div>
            </div>
        {/if}
    </div>

    <div class="home-content">
        <!-- Sección 1: Consulta por fecha -->
        <DateSearch />

        <!-- Sección 2: Resultados de la última consulta -->
        <RecentResultsTable />

        <!-- Sección 3: Todas las constituciones -->
        <div class="all-constitutions-section">
            <div class="section-header">
                <h2>Todas las Constituciones Almacenadas</h2>
                <div class="section-info">
                    <span class="company-count">Total: {$companies.length} constituciones</span>
                    {#if !$initialLoadComplete}
                        <span class="loading-badge">Cargando...</span>
                    {/if}
                </div>
            </div>

            {#if error}
                <div class="error-message">{error}</div>
            {:else}
                <CompanyTable />
            {/if}
        </div>
    </div>
</div>

<style>
    .home {
        flex: 1;
        display: flex;
        flex-direction: column;
        width: 100%;
        min-height: 100vh;
    }

    .home-header {
        background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
        color: white;
        padding: 2rem 1rem;
        text-align: center;
        border-bottom: 1px solid #e2e8f0;
        flex-shrink: 0;
    }

    h1 {
        margin: 0 0 0.75rem 0;
        font-size: 2.25rem;
        font-weight: 700;
    }

    .subtitle {
        margin: 0 0 1.5rem 0;
        font-size: 1.125rem;
        opacity: 0.9;
        font-weight: 300;
        max-width: 600px;
        margin: 0 auto 1.5rem auto;
    }

    .stats-bar {
        display: flex;
        justify-content: center;
        gap: 3rem;
        flex-wrap: wrap;
        margin-top: 1rem;
    }

    .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .stat-number {
        font-size: 2rem;
        font-weight: 700;
        margin-bottom: 0.25rem;
    }

    .stat-label {
        font-size: 0.875rem;
        opacity: 0.8;
    }

    .home-content {
        flex: 1;
        padding: 1.5rem;
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
        width: 100%;
        min-height: 0;
    }

    .all-constitutions-section {
        background: white;
        border-radius: 0.5rem;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
        overflow: hidden;
        flex: 1;
        display: flex;
        flex-direction: column;
        min-height: 0;
    }

    .section-header {
        background: #f8fafc;
        padding: 1.25rem 1.5rem;
        border-bottom: 1px solid #e2e8f0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 1rem;
        flex-shrink: 0;
    }

    .section-header h2 {
        margin: 0;
        color: #1e293b;
        font-size: 1.375rem;
    }

    .section-info {
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .company-count {
        background: #3b82f6;
        color: white;
        padding: 0.5rem 1rem;
        border-radius: 2rem;
        font-size: 0.875rem;
        font-weight: 600;
    }

    .loading-badge {
        background: #f59e0b;
        color: white;
        padding: 0.5rem 1rem;
        border-radius: 2rem;
        font-size: 0.875rem;
        font-weight: 600;
    }

    .error-message {
        padding: 2rem;
        text-align: center;
        color: #dc2626;
        background: #fef2f2;
        border: 1px solid #fecaca;
        margin: 2rem;
        border-radius: 0.5rem;
    }

    @media (max-width: 768px) {
        .home-header {
            padding: 1.5rem 1rem;
        }

        h1 {
            font-size: 1.75rem;
        }

        .subtitle {
            font-size: 1rem;
        }

        .stats-bar {
            gap: 1.5rem;
        }

        .stat-number {
            font-size: 1.5rem;
        }

        .home-content {
            padding: 1rem;
            gap: 1rem;
        }

        .section-header {
            padding: 1rem;
            flex-direction: column;
            align-items: stretch;
        }

        .section-header h2 {
            font-size: 1.25rem;
        }
    }
</style>