<script>
    import { searchCompaniesByDate, loading, lastSearchedDate, companies } from '$lib/stores/companies';

    let searchDate = '';
    let searchError = '';
    let searchSuccess = '';

    async function handleDateSearch() {
        if (!searchDate) {
            searchError = 'Por favor, selecciona una fecha';
            return;
        }

        searchError = '';
        searchSuccess = '';

        try {
            console.log('Starting search for date:', searchDate);
            const newCompanies = await searchCompaniesByDate(searchDate);
            console.log('Search completed, received:', newCompanies);

            if (newCompanies && newCompanies.length > 0) {
                searchSuccess = `✅ Se encontraron ${newCompanies.length} constituciones para la fecha ${searchDate}. Tabla actualizada.`;
            } else {
                searchSuccess = `ℹ️ No se encontraron constituciones para la fecha ${searchDate}.`;
            }
        } catch (error) {
            console.error('Search error details:', error);
            searchError = '❌ Error al consultar el BORME: ' + (error.message || 'Error de conexión. Verifica que el backend esté funcionando.');
        }
    }

    function clearSearch() {
        searchDate = '';
        searchError = '';
        searchSuccess = '';
    }

    // Formatear fecha actual para el placeholder
    const today = new Date().toISOString().split('T')[0];
</script>

<div class="date-search-section">
    <h2>Consultar Constituciones del BORME por Fecha</h2>

    <div class="search-controls">
        <div class="input-group">
            <label for="search-date">Selecciona una fecha (YYYY-MM-DD):</label>
            <input
                    id="search-date"
                    type="date"
                    bind:value={searchDate}
                    class="date-input"
                    max={today}
            />
        </div>

        <div class="button-group">
            <button
                    on:click={handleDateSearch}
                    disabled={$loading || !searchDate}
                    class="search-btn"
            >
                {#if $loading}
                    🔄 Consultando BORME...
                {:else}
                    🔍 Consultar BORME
                {/if}
            </button>

            <button on:click={clearSearch} class="clear-btn">
                Limpiar
            </button>
        </div>
    </div>

    {#if searchSuccess}
        <div class="message success">
            {searchSuccess}
        </div>
    {/if}

    {#if searchError}
        <div class="message error">
            {searchError}
        </div>
    {/if}

    {#if $lastSearchedDate}
        <div class="last-search">
            Última consulta: <strong>{$lastSearchedDate}</strong>
        </div>
    {/if}
</div>

<style>
    .date-search-section {
        background: white;
        padding: 2rem;
        border-radius: 0.5rem;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
        margin-bottom: 1.5rem;
    }

    h2 {
        color: #1e293b;
        margin: 0 0 1.5rem 0;
        font-size: 1.5rem;
        text-align: center;
    }

    .search-controls {
        display: flex;
        gap: 2rem;
        align-items: end;
        flex-wrap: wrap;
    }

    .input-group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        flex: 1;
        min-width: 200px;
    }

    .input-group label {
        font-weight: 600;
        color: #374151;
        font-size: 0.875rem;
    }

    .date-input {
        padding: 0.75rem;
        border: 2px solid #d1d5db;
        border-radius: 0.5rem;
        font-size: 1rem;
        transition: border-color 0.2s;
    }

    .date-input:focus {
        outline: none;
        border-color: #3b82f6;
        box-shadow: 0 0 0 3px rgb(59 130 246 / 0.1);
    }

    .button-group {
        display: flex;
        gap: 1rem;
        align-items: center;
    }

    .search-btn {
        padding: 0.75rem 1.5rem;
        background-color: #3b82f6;
        color: white;
        border: none;
        border-radius: 0.5rem;
        cursor: pointer;
        font-weight: 600;
        font-size: 0.875rem;
        transition: background-color 0.2s;
        min-width: 160px;
    }

    .search-btn:hover:not(:disabled) {
        background-color: #2563eb;
    }

    .search-btn:disabled {
        background-color: #9ca3af;
        cursor: not-allowed;
    }

    .clear-btn {
        padding: 0.75rem 1.5rem;
        background-color: #6b7280;
        color: white;
        border: none;
        border-radius: 0.5rem;
        cursor: pointer;
        font-size: 0.875rem;
    }

    .clear-btn:hover {
        background-color: #4b5563;
    }

    .message {
        margin-top: 1rem;
        padding: 1rem;
        border-radius: 0.375rem;
        font-weight: 500;
    }

    .success {
        background-color: #d1fae5;
        color: #065f46;
        border: 1px solid #a7f3d0;
    }

    .error {
        background-color: #fef2f2;
        color: #dc2626;
        border: 1px solid #fecaca;
    }

    .last-search {
        margin-top: 1rem;
        padding: 0.75rem;
        background-color: #f8fafc;
        border-radius: 0.375rem;
        font-size: 0.875rem;
        color: #6b7280;
        text-align: center;
        border: 1px solid #e5e7eb;
    }

    @media (max-width: 768px) {
        .search-controls {
            flex-direction: column;
            align-items: stretch;
        }

        .button-group {
            justify-content: center;
        }
    }
</style>