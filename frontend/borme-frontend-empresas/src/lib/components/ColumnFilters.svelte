<script>
    import { filters, updateFilter, clearFilters } from '$lib/stores/companies';

    function handleFilterChange(key, event) {
        updateFilter(key, event.target.value);
    }
</script>

<div class="filters-row">
    <div class="filter-cell">
        <input
                type="text"
                value={$filters.numeroAsiento}
                on:input={(e) => handleFilterChange('numeroAsiento', e)}
                placeholder="Filtrar asiento..."
                class="filter-input"
        />
    </div>

    <div class="filter-cell">
        <input
                type="date"
                value={$filters.fechaConstitucion}
                on:input={(e) => handleFilterChange('fechaConstitucion', e)}
                class="filter-input"
        />
    </div>

    <div class="filter-cell">
        <input
                type="text"
                value={$filters.nombreEmpresa}
                on:input={(e) => handleFilterChange('nombreEmpresa', e)}
                placeholder="Filtrar nombre..."
                class="filter-input"
        />
    </div>

    <div class="filter-cell">
        <input
                type="text"
                value={$filters.objetoSocial}
                on:input={(e) => handleFilterChange('objetoSocial', e)}
                placeholder="Filtrar objeto social..."
                class="filter-input"
        />
    </div>

    <div class="filter-cell">
        <input
                type="text"
                value={$filters.domicilio}
                on:input={(e) => handleFilterChange('domicilio', e)}
                placeholder="Filtrar domicilio..."
                class="filter-input"
        />
    </div>

    <div class="filter-cell">
        <input
                type="text"
                value={$filters.capital}
                on:input={(e) => handleFilterChange('capital', e)}
                placeholder="Filtrar capital..."
                class="filter-input"
        />
    </div>

    <div class="filter-cell actions">
        <button on:click={clearFilters} class="clear-filters-btn" title="Limpiar todos los filtros">
            🗑️
        </button>
    </div>
</div>

<style>
    .filters-row {
        display: contents;
    }

    .filter-cell {
        padding: 0.5rem;
        background: #f8fafc;
        border-bottom: 2px solid #e2e8f0;
    }

    .filter-cell.actions {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .filter-input {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #d1d5db;
        border-radius: 0.375rem;
        font-size: 0.875rem;
        background: white;
    }

    .filter-input:focus {
        outline: none;
        border-color: #3b82f6;
        box-shadow: 0 0 0 2px rgb(59 130 246 / 0.1);
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
</style>