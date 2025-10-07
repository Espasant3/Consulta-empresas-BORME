<script>
    import Home from './routes/Home.svelte';

    let currentView = 'home';
    let selectedCompanyId = null;

    function showCompanyDetail(companyId) {
        selectedCompanyId = companyId;
        currentView = 'detail';
    }

    function goHome() {
        currentView = 'home';
        selectedCompanyId = null;
    }
</script>

<div class="app">
    <header class="header">
        <nav class="nav">
            <button on:click={goHome} class="logo-btn">
                📊 BORME Empresas
            </button>
        </nav>
    </header>

    <main class="main">
        {#if currentView === 'home'}
            <Home on:companySelected={showCompanyDetail} />
        {:else if currentView === 'detail'}
            {#if selectedCompanyId}
                <div class="detail-view">
                    <button on:click={goHome} class="back-btn">← Volver a la lista</button>
                    <div class="detail-placeholder">
                        <h2>Detalles de la empresa</h2>
                        <p>ID: {selectedCompanyId}</p>
                        <p>Aquí irían los detalles completos de la empresa...</p>
                    </div>
                </div>
            {/if}
        {/if}
    </main>
</div>

<style>
    .app {
        min-height: 100vh;
        background-color: #f8fafc;
        display: flex;
        flex-direction: column;
    }

    .header {
        background-color: white;
        border-bottom: 1px solid #e2e8f0;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
        flex-shrink: 0;
    }

    .nav {
        width: 100%;
        max-width: none;
        margin: 0 auto;
        padding: 1rem 2rem;
    }

    .logo-btn {
        font-size: 1.5rem;
        font-weight: bold;
        color: #1e293b;
        background: none;
        border: none;
        cursor: pointer;
        padding: 0;
    }

    .logo-btn:hover {
        color: #3b82f6;
    }

    .main {
        flex: 1;
        width: 100%;
        padding: 0;
        display: flex;
        flex-direction: column;
    }

    .back-btn {
        background: #6b7280;
        color: white;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 0.375rem;
        cursor: pointer;
        margin: 1rem 2rem;
        font-size: 0.875rem;
    }

    .back-btn:hover {
        background: #4b5563;
    }

    .detail-view {
        flex: 1;
        display: flex;
        flex-direction: column;
    }

    .detail-placeholder {
        flex: 1;
        padding: 2rem;
        background: white;
        margin: 0 2rem 2rem 2rem;
        border-radius: 0.5rem;
        box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
    }
</style>