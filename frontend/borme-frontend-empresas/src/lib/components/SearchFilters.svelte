<script>
    import { searchTerm, selectedDate } from '$lib/stores/companies';

    let localSearch = '';
    let localDate = '';

    function applyFilters() {
        $searchTerm = localSearch;
        $selectedDate = localDate;
    }

    function clearFilters() {
        localSearch = '';
        localDate = '';
        $searchTerm = '';
        $selectedDate = '';
    }
</script>

<div class="filters-container">
    <div class="filters">
        <div class="filter-group">
            <label for="search">Buscar:</label>
            <input
                    id="search"
                    type="text"
                    bind:value={localSearch}
                    placeholder="Nombre, objeto social, domicilio..."
                    on:input={applyFilters}
            />
        </div>

        <div class="filter-group">
            <label for="date">Fecha:</label>
            <input
                    id="date"
                    type="date"
                    bind:value={localDate}
                    on:change={applyFilters}
            />
        </div>

        <button on:click={clearFilters} class="clear-btn">
            Limpiar filtros
        </button>
    </div>
</div>

<style>
    .filters-container {
        background: white;
        padding: 1.5rem;
        border-radius: 0.5rem;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
    }

    .filters {
        display: flex;
        gap: 1.5rem;
        align-items: end;
        flex-wrap: wrap;
    }

    .filter-group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        flex: 1;
        min-width: 200px;
    }

    .filter-group label {
        font-weight: 600;
        color: #374151;
        font-size: 0.875rem;
    }

    .filter-group input {
        padding: 0.5rem 0.75rem;
        border: 1px solid #d1d5db;
        border-radius: 0.375rem;
        width: 75%;
        font-size: 0.875rem;
    }

    .filter-group input:focus {
        outline: none;
        border-color: #3b82f6;
        box-shadow: 0 0 0 3px rgb(59 130 246 / 0.1);
    }

    .clear-btn {
        padding: 0.5rem 1rem;
        background-color: #6b7280;
        color: white;
        border: none;
        border-radius: 0.375rem;
        cursor: pointer;
        font-size: 0.875rem;
        height: fit-content;
        white-space: nowrap;
    }

    .clear-btn:hover {
        background-color: #4b5563;
    }
</style>