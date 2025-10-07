<script>
    import { filteredCompanies } from '$lib/stores/companies';

    function handleRowClick(company) {
        dispatch('companySelected', { companyId: company.id });
    }
</script>

<div class="table-wrapper">
    <div class="table-container">
        <table class="company-table">
            <thead>
            <tr>
                <th class="col-name">Nombre</th>
                <th class="col-date">Fecha Constitución</th>
                <th class="col-object">Objeto Social</th>
                <th class="col-address">Domicilio</th>
                <th class="col-capital">Capital</th>
            </tr>
            </thead>
            <tbody>
            {#each $filteredCompanies as company (company.id)}
                <tr on:click={() => handleRowClick(company)} class="clickable-row">
                    <td class="col-name company-name">{company.name}</td>
                    <td class="col-date company-data">{company.creationDate}</td>
                    <td class="col-object company-data">{company.businessObject?.substring(0, 80)}...</td>
                    <td class="col-address company-data">{company.address}</td>
                    <td class="col-capital company-data">{company.capital} €</td>
                </tr>
            {/each}
            </tbody>
        </table>

        {#if $filteredCompanies.length === 0}
            <div class="no-results">
                No se encontraron empresas que coincidan con los filtros
            </div>
        {/if}
    </div>
</div>

<style>
    .table-wrapper {
        flex: 1;
        display: flex;
        flex-direction: column;
        min-height: 400px; /* Altura mínima para mantener consistencia */
    }

    .table-container {
        flex: 1;
        display: flex;
        flex-direction: column;
        background: white;
        border-radius: 0.5rem;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
        overflow: hidden;
        min-height: 400px;
    }

    .company-table {
        width: 100%;
        border-collapse: collapse;
        color: #1f2937;
        table-layout: fixed; /* Esto hace que las columnas mantengan su ancho */
    }

    .company-table th,
    .company-table td {
        padding: 1rem;
        text-align: left;
        border-bottom: 1px solid #e2e8f0;
        vertical-align: top;
    }

    .company-table th {
        background-color: #f8fafc;
        font-weight: 600;
        color: #374151;
        position: sticky;
        top: 0;
        z-index: 10;
    }

    .company-table td {
        color: #1f2937;
        background-color: white;
    }

    /* Definir anchos fijos para las columnas */
    .col-name { width: 25%; }
    .col-date { width: 15%; }
    .col-object { width: 30%; }
    .col-address { width: 20%; }
    .col-capital { width: 10%; }

    .clickable-row {
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .clickable-row:hover {
        background-color: #f1f5f9;
    }

    .clickable-row:hover td {
        background-color: #f1f5f9;
    }

    .company-name {
        font-weight: 600;
        color: #1e40af;
    }

    .company-data {
        color: #1f2937;
    }

    /* Asegurar que el texto largo se maneje bien */
    .col-object,
    .col-address {
        word-wrap: break-word;
        overflow-wrap: break-word;
    }

    .no-results {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 3rem;
        color: #64748b;
        font-style: italic;
        text-align: center;
        min-height: 200px;
    }
</style>