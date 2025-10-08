<script>
    import { recentSearchResults, expandedCompanyId, toggleCompanyExpansion, generateCompanyId } from '$lib/stores/companies';
    import { companyService } from '$lib/services/api';

    let recentFilters = {
        numeroAsiento: '',
        fechaConstitucion: '',
        nombreEmpresa: '',
        objetoSocial: '',
        domicilio: '',
        capital: ''
    };

    function handleRowClick(company) {
        const companyId = generateCompanyId(company);
        toggleCompanyExpansion(companyId);
    }

    function truncateText(text, maxLength = 50) {
        if (!text) return '';
        return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    }

    function handleFilterChange(key, event) {
        recentFilters = { ...recentFilters, [key]: event.target.value };
    }

    function clearRecentFilters() {
        recentFilters = {
            numeroAsiento: '',
            fechaConstitucion: '',
            nombreEmpresa: '',
            objetoSocial: '',
            domicilio: '',
            capital: ''
        };
    }

    // Filtrado local de resultados recientes
    $: filteredRecentResults = $recentSearchResults ? $recentSearchResults.filter(company => {
        return Object.entries(recentFilters).every(([key, value]) => {
            if (!value) return true;
            const companyValue = company[key] || '';
            return companyValue.toString().toLowerCase().includes(value.toLowerCase());
        });
    }) : [];
</script>

<div class="recent-results-section">
    <div class="section-header">
        <h3>Resultados de la Última Consulta</h3>
        <div class="section-info">
            <span class="company-count">Encontrados: {filteredRecentResults.length}</span>
        </div>
    </div>

    <div class="table-container">
        <div class="table-scroll-container">
            <div class="table-grid">
                <!-- Headers -->
                <div class="header-cell">Nº Asiento</div>
                <div class="header-cell">Fecha Const.</div>
                <div class="header-cell">Nombre Empresa</div>
                <div class="header-cell">Objeto Social</div>
                <div class="header-cell">Domicilio</div>
                <div class="header-cell">Capital</div>
                <div class="header-cell">Acciones</div>

                <!-- Filtros -->
                <div class="filters-row">
                    <div class="filter-cell">
                        <input
                                type="text"
                                value={recentFilters.numeroAsiento}
                                on:input={(e) => handleFilterChange('numeroAsiento', e)}
                                placeholder="Filtrar asiento..."
                                class="filter-input"
                        />
                    </div>

                    <div class="filter-cell">
                        <input
                                type="date"
                                value={recentFilters.fechaConstitucion}
                                on:input={(e) => handleFilterChange('fechaConstitucion', e)}
                                class="filter-input"
                        />
                    </div>

                    <div class="filter-cell">
                        <input
                                type="text"
                                value={recentFilters.nombreEmpresa}
                                on:input={(e) => handleFilterChange('nombreEmpresa', e)}
                                placeholder="Filtrar nombre..."
                                class="filter-input"
                        />
                    </div>

                    <div class="filter-cell">
                        <input
                                type="text"
                                value={recentFilters.objetoSocial}
                                on:input={(e) => handleFilterChange('objetoSocial', e)}
                                placeholder="Filtrar objeto social..."
                                class="filter-input"
                        />
                    </div>

                    <div class="filter-cell">
                        <input
                                type="text"
                                value={recentFilters.domicilio}
                                on:input={(e) => handleFilterChange('domicilio', e)}
                                placeholder="Filtrar domicilio..."
                                class="filter-input"
                        />
                    </div>

                    <div class="filter-cell">
                        <input
                                type="text"
                                value={recentFilters.capital}
                                on:input={(e) => handleFilterChange('capital', e)}
                                placeholder="Filtrar capital..."
                                class="filter-input"
                        />
                    </div>

                    <div class="filter-cell actions">
                        <button on:click={clearRecentFilters} class="clear-filters-btn" title="Limpiar filtros">
                            🗑️
                        </button>
                    </div>
                </div>

                <!-- Filas de datos -->
                {#if filteredRecentResults.length > 0}
                    {#each filteredRecentResults as company}
                        <div class="data-row {$expandedCompanyId === generateCompanyId(company) ? 'expanded' : ''}">
                            <div class="data-cell" title={company.numeroAsiento}>
                                {company.numeroAsiento}
                            </div>
                            <div class="data-cell" title={company.fechaConstitucion}>
                                {company.fechaConstitucion}
                            </div>
                            <div class="data-cell company-name" title={company.nombreEmpresa}>
                                {truncateText(company.nombreEmpresa, 30)}
                            </div>
                            <div class="data-cell" title={company.objetoSocial}>
                                {truncateText(company.objetoSocial, 40)}
                            </div>
                            <div class="data-cell" title={company.domicilio}>
                                {truncateText(company.domicilio, 30)}
                            </div>
                            <div class="data-cell" title={company.capital}>
                                {company.capital} €
                            </div>
                            <div class="data-cell actions">
                                <button
                                        on:click={() => handleRowClick(company)}
                                        class="expand-btn"
                                        title="{$expandedCompanyId === generateCompanyId(company) ? 'Contraer detalles' : 'Expandir detalles'}"
                                >
                                    {$expandedCompanyId === generateCompanyId(company) ? '▲' : '▼'}
                                </button>
                            </div>
                        </div>

                        <!-- Fila expandida con detalles -->
                        {#if $expandedCompanyId === generateCompanyId(company)}
                            <div class="expanded-details">
                                <div class="details-content">
                                    <div class="details-header">
                                        <h4>Detalles Completos - {company.nombreEmpresa}</h4>
                                        <button on:click={() => toggleCompanyExpansion(generateCompanyId(company))} class="close-btn">
                                            ✕
                                        </button>
                                    </div>

                                    <div class="details-grid">
                                        <div class="detail-item">
                                            <label>Número de Asiento:</label>
                                            <span>{company.numeroAsiento}</span>
                                        </div>
                                        <div class="detail-item">
                                            <label>Fecha de Constitución:</label>
                                            <span>{company.fechaConstitucion}</span>
                                        </div>
                                        <div class="detail-item full-width">
                                            <label>Nombre de la Empresa:</label>
                                            <span>{company.nombreEmpresa}</span>
                                        </div>
                                        <div class="detail-item full-width">
                                            <label>Objeto Social:</label>
                                            <span class="business-object">{company.objetoSocial}</span>
                                        </div>
                                        <div class="detail-item full-width">
                                            <label>Domicilio:</label>
                                            <span>{company.domicilio}</span>
                                        </div>
                                        <div class="detail-item">
                                            <label>Capital Social:</label>
                                            <span>{company.capital} €</span>
                                        </div>
                                        <div class="detail-item">
                                            <label>Fecha del PDF:</label>
                                            <span>{company.fechaPDF || 'No disponible'}</span>
                                        </div>
                                        <div class="detail-item full-width">
                                            <label>Archivo PDF:</label>
                                            <span>
                        {#if companyService.getPdfUrl(company)}
                          <a
                                  href={companyService.getPdfUrl(company)}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  class="pdf-link"
                                  title="Abrir PDF original en nueva pestaña"
                                  on:click|preventDefault={(e) => {
                              e.preventDefault();
                              window.open(companyService.getPdfUrl(company), '_blank');
                            }}
                          >
                            📄 {company.nombreArchivoPDF || 'Ver PDF'}
                          </a>
                        {:else}
                          <span class="no-pdf">PDF no disponible</span>
                        {/if}
                      </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        {/if}
                    {/each}
                {/if}
            </div>
        </div>

        {#if filteredRecentResults.length === 0}
            <div class="no-results">
                {#if $recentSearchResults.length === 0}
                    <div class="empty-state">
                        <h3>No se han realizado consultas</h3>
                        <p>Utiliza el formulario superior para consultar constituciones por fecha del BORME</p>
                    </div>
                {:else}
                    <div class="no-matches">
                        <h3>No se encontraron coincidencias</h3>
                        <p>Prueba a ajustar los filtros de búsqueda</p>
                    </div>
                {/if}
            </div>
        {/if}
    </div>
</div>

<style>
    .recent-results-section {
        background: white;
        border-radius: 0.5rem;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
        overflow: hidden;
        margin-bottom: 1.5rem;
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
    }

    .section-header h3 {
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
        background: #10b981;
        color: white;
        padding: 0.5rem 1rem;
        border-radius: 2rem;
        font-size: 0.875rem;
        font-weight: 600;
    }

    .table-container {
        display: flex;
        flex-direction: column;
        min-height: 200px;
    }

    .table-scroll-container {
        overflow-x: auto;
    }

    .table-grid {
        display: grid;
        grid-template-columns: 0.8fr 0.8fr 1.5fr 2fr 1.5fr 0.8fr 0.5fr;
        min-width: 1000px;
    }

    .header-cell {
        background: #059669;
        color: white;
        padding: 0.75rem;
        font-weight: 600;
        font-size: 0.875rem;
        text-align: left;
        border-bottom: 1px solid #047857;
        position: sticky;
        top: 0;
        z-index: 10;
    }

    .filters-row {
        display: contents;
    }

    .filter-cell {
        padding: 0.5rem;
        background: #f0fdf4;
        border-bottom: 2px solid #dcfce7;
    }

    .filter-cell.actions {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .filter-input {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #bbf7d0;
        border-radius: 0.375rem;
        font-size: 0.875rem;
        background: white;
    }

    .filter-input:focus {
        outline: none;
        border-color: #10b981;
        box-shadow: 0 0 0 2px rgb(16 185 129 / 0.1);
    }

    .clear-filters-btn {
        background: #6b7280;
        color: white;
        border: none;
        border-radius: 0.375rem;
        padding: 0.5rem;
        cursor: pointer;
        font-size: 0.875rem;
    }

    .clear-filters-btn:hover {
        background: #4b5563;
    }

    .table-wrapper {
        width: 100%;
        margin: 0;
        display: flex;
        flex-direction: column;
        flex: 1;
        min-height: 0;
    }

    .table-container {
        flex: 1;
        display: flex;
        flex-direction: column;
        background: white;
        border-radius: 0.5rem;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
        overflow: hidden;
        min-height: 0; /* ✅ clave */
    }

    .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 1.5rem;
        background: #f8fafc;
        border-bottom: 1px solid #e2e8f0;
    }

    .table-header h3 {
        margin: 0;
        color: #1e293b;
        font-size: 1.25rem;
    }

    .table-stats {
        display: flex;
        gap: 1rem;
        align-items: center;
    }

    .count-badge {
        background: #3b82f6;
        color: white;
        padding: 0.25rem 0.75rem;
        border-radius: 1rem;
        font-size: 0.875rem;
        font-weight: 500;
    }

    .table-scroll-container {
        overflow-x: auto;
    }

    .table-grid {
        display: grid;
        grid-template-columns: 0.8fr 0.8fr 1.5fr 2fr 1.5fr 0.8fr 0.5fr;
        min-width: 1000px;
    }

    .header-cell {
        background: #1e40af;
        color: white;
        padding: 0.75rem;
        font-weight: 600;
        font-size: 0.875rem;
        text-align: left;
        border-bottom: 1px solid #3730a3;
        position: sticky;
        top: 0;
        z-index: 10;
    }

    .data-row {
        display: contents;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .data-row:hover .data-cell {
        background-color: #f8fafc;
    }

    .data-row.expanded .data-cell {
        background-color: #e0f2fe;
        font-weight: 500;
    }

    .data-cell {
        padding: 0.75rem;
        border-bottom: 1px solid #e2e8f0;
        background: white;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        font-size: 0.875rem;
        color: #374151;
        transition: background-color 0.2s;
    }

    .company-name {
        color: #1e40af;
        font-weight: 500;
    }

    .actions {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .expand-btn {
        background: #6b7280;
        color: white;
        border: none;
        border-radius: 0.25rem;
        padding: 0.25rem 0.5rem;
        cursor: pointer;
        font-size: 0.75rem;
        transition: background-color 0.2s;
    }

    .expand-btn:hover {
        background: #4b5563;
    }

    .expanded-details {
        grid-column: 1 / -1;
        background: #f8fafc;
        border-bottom: 1px solid #e2e8f0;
    }

    .details-content {
        padding: 1.5rem;
    }

    .details-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .details-header h4 {
        margin: 0;
        color: #1e40af;
        font-size: 1.125rem;
    }

    .close-btn {
        background: #6b7280;
        color: white;
        border: none;
        border-radius: 0.25rem;
        padding: 0.5rem;
        cursor: pointer;
        font-size: 0.875rem;
    }

    .close-btn:hover {
        background: #4b5563;
    }

    .details-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
    }

    .detail-item {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .detail-item.full-width {
        grid-column: 1 / -1;
    }

    .detail-item label {
        font-weight: 600;
        color: #374151;
        font-size: 0.875rem;
    }

    .detail-item span {
        color: #1f2937;
    }

    .business-object {
        line-height: 1.5;
        white-space: normal;
    }

    .pdf-link {
        color: #3b82f6;
        text-decoration: none;
        font-weight: 500;
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
    }

    .pdf-link:hover {
        text-decoration: underline;
    }

    .no-pdf {
        color: #6b7280;
        font-style: italic;
    }

    .no-results {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 3rem;
        color: #64748b;
        text-align: center;
        min-height: 200px;
    }

    .empty-state h3,
    .no-matches h3 {
        color: #374151;
        margin-bottom: 0.5rem;
    }

    .empty-state p,
    .no-matches p {
        color: #6b7280;
        margin: 0;
    }

    /* Responsive */
    @media (max-width: 1200px) {
        .table-wrapper {
            width: 98%;
        }
    }
</style>