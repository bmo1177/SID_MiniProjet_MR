/*
 * Pool de connexions optimisé pour production
 * Gestion avancée des connexions base de données
 */
package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import utils.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {
    private static ConnectionPool instance;
    private HikariDataSource dataSource;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicInteger totalConnections = new AtomicInteger(0);
    
    private ConnectionPool() {
        initializePool();
    }
    
    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }
    
    private void initializePool() {
        try {
            AppProperties config = AppProperties.getInstance();
            
            HikariConfig poolConfig = new HikariConfig();
            
            // Configuration de base
            poolConfig.setJdbcUrl(config.getDatabaseUrl());
            poolConfig.setUsername(config.getProperty("db.username"));
            poolConfig.setPassword(config.getProperty("db.password"));
            poolConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            // Configuration du pool
            poolConfig.setMinimumIdle(config.getIntProperty("db.pool.min", 5));
            poolConfig.setMaximumPoolSize(config.getIntProperty("db.pool.max", 20));
            poolConfig.setConnectionTimeout(config.getIntProperty("db.pool.timeout", 30000));
            poolConfig.setIdleTimeout(600000); // 10 minutes
            poolConfig.setMaxLifetime(1800000); // 30 minutes
            poolConfig.setLeakDetectionThreshold(60000); // 1 minute
            
            // Configuration avancée
            poolConfig.setPoolName("GestionScolaritePool");
            poolConfig.setConnectionTestQuery("SELECT 1");
            poolConfig.setValidationTimeout(5000);
            
            // Propriétés MySQL optimisées
            poolConfig.addDataSourceProperty("cachePrepStmts", "true");
            poolConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            poolConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            poolConfig.addDataSourceProperty("useServerPrepStmts", "true");
            poolConfig.addDataSourceProperty("useLocalSessionState", "true");
            poolConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
            poolConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
            poolConfig.addDataSourceProperty("cacheServerConfiguration", "true");
            poolConfig.addDataSourceProperty("elideSetAutoCommits", "true");
            poolConfig.addDataSourceProperty("maintainTimeStats", "false");
            
            // Initialiser le pool
            dataSource = new HikariDataSource(poolConfig);
            
            Logger.info("✅ Pool de connexions initialisé avec succès");
            Logger.info("   Pool size: " + poolConfig.getMinimumIdle() + "-" + poolConfig.getMaximumPoolSize());
            Logger.info("   URL: " + config.getDatabaseUrl());
            
        } catch (Exception e) {
            Logger.error("❌ Erreur initialisation pool de connexions: " + e.getMessage());
            throw new RuntimeException("Impossible d'initialiser le pool de connexions", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Pool de connexions non initialisé");
        }
        
        try {
            Connection connection = dataSource.getConnection();
            activeConnections.incrementAndGet();
            totalConnections.incrementAndGet();
            
            if (AppProperties.getInstance().isDebugMode()) {
                Logger.debug("🔗 Connexion obtenue. Actives: " + activeConnections.get());
            }
            
            return new ConnectionWrapper(connection, this);
            
        } catch (SQLException e) {
            Logger.error("❌ Erreur obtention connexion: " + e.getMessage());
            throw e;
        }
    }
    
    void releaseConnection() {
        activeConnections.decrementAndGet();
        
        if (AppProperties.getInstance().isDebugMode()) {
            Logger.debug("🔓 Connexion libérée. Actives: " + activeConnections.get());
        }
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public boolean isHealthy() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            Logger.warning("⚠️ Vérification santé pool échouée: " + e.getMessage());
            return false;
        }
    }
    
    public PoolStats getStats() {
        if (dataSource == null) {
            return new PoolStats(0, 0, 0, 0);
        }
        
        return new PoolStats(
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            totalConnections.get()
        );
    }
    
    public void printStats() {
        if (dataSource != null) {
            PoolStats stats = getStats();
            Logger.info("📊 Statistiques du pool:");
            Logger.info("   Connexions actives: " + stats.activeConnections);
            Logger.info("   Connexions inactives: " + stats.idleConnections);
            Logger.info("   Total connexions: " + stats.totalConnections);
            Logger.info("   Connexions créées: " + stats.totalConnectionsCreated);
        }
    }
    
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            Logger.info("🔄 Fermeture du pool de connexions...");
            dataSource.close();
            Logger.info("✅ Pool de connexions fermé");
        }
    }
    
    // Classe pour wrapper les connexions et traquer leur utilisation
    private static class ConnectionWrapper implements Connection {
        private final Connection delegate;
        private final ConnectionPool pool;
        private boolean closed = false;
        
        public ConnectionWrapper(Connection delegate, ConnectionPool pool) {
            this.delegate = delegate;
            this.pool = pool;
        }
        
        @Override
        public void close() throws SQLException {
            if (!closed) {
                delegate.close();
                pool.releaseConnection();
                closed = true;
            }
        }
        
        @Override
        public boolean isClosed() throws SQLException {
            return closed || delegate.isClosed();
        }
        
        // Délégation de toutes les autres méthodes
        @Override
        public java.sql.Statement createStatement() throws SQLException {
            return delegate.createStatement();
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            return delegate.prepareStatement(sql);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            return delegate.prepareCall(sql);
        }
        
        @Override
        public String nativeSQL(String sql) throws SQLException {
            return delegate.nativeSQL(sql);
        }
        
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            delegate.setAutoCommit(autoCommit);
        }
        
        @Override
        public boolean getAutoCommit() throws SQLException {
            return delegate.getAutoCommit();
        }
        
        @Override
        public void commit() throws SQLException {
            delegate.commit();
        }
        
        @Override
        public void rollback() throws SQLException {
            delegate.rollback();
        }
        
        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            return delegate.getMetaData();
        }
        
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            delegate.setReadOnly(readOnly);
        }
        
        @Override
        public boolean isReadOnly() throws SQLException {
            return delegate.isReadOnly();
        }
        
        @Override
        public void setCatalog(String catalog) throws SQLException {
            delegate.setCatalog(catalog);
        }
        
        @Override
        public String getCatalog() throws SQLException {
            return delegate.getCatalog();
        }
        
        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            delegate.setTransactionIsolation(level);
        }
        
        @Override
        public int getTransactionIsolation() throws SQLException {
            return delegate.getTransactionIsolation();
        }
        
        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            return delegate.getWarnings();
        }
        
        @Override
        public void clearWarnings() throws SQLException {
            delegate.clearWarnings();
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.createStatement(resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
            return delegate.getTypeMap();
        }
        
        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
            delegate.setTypeMap(map);
        }
        
        @Override
        public void setHoldability(int holdability) throws SQLException {
            delegate.setHoldability(holdability);
        }
        
        @Override
        public int getHoldability() throws SQLException {
            return delegate.getHoldability();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            return delegate.setSavepoint();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            return delegate.setSavepoint(name);
        }
        
        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            delegate.rollback(savepoint);
        }
        
        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            delegate.releaseSavepoint(savepoint);
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return delegate.prepareStatement(sql, autoGeneratedKeys);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return delegate.prepareStatement(sql, columnIndexes);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return delegate.prepareStatement(sql, columnNames);
        }
        
        @Override
        public java.sql.Clob createClob() throws SQLException {
            return delegate.createClob();
        }
        
        @Override
        public java.sql.Blob createBlob() throws SQLException {
            return delegate.createBlob();
        }
        
        @Override
        public java.sql.NClob createNClob() throws SQLException {
            return delegate.createNClob();
        }
        
        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            return delegate.createSQLXML();
        }
        
        @Override
        public boolean isValid(int timeout) throws SQLException {
            return delegate.isValid(timeout);
        }
        
        @Override
        public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
            delegate.setClientInfo(name, value);
        }
        
        @Override
        public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
            delegate.setClientInfo(properties);
        }
        
        @Override
        public String getClientInfo(String name) throws SQLException {
            return delegate.getClientInfo(name);
        }
        
        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            return delegate.getClientInfo();
        }
        
        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return delegate.createArrayOf(typeName, elements);
        }
        
        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return delegate.createStruct(typeName, attributes);
        }
        
        @Override
        public void setSchema(String schema) throws SQLException {
            delegate.setSchema(schema);
        }
        
        @Override
        public String getSchema() throws SQLException {
            return delegate.getSchema();
        }
        
        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            delegate.abort(executor);
        }
        
        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
            delegate.setNetworkTimeout(executor, milliseconds);
        }
        
        @Override
        public int getNetworkTimeout() throws SQLException {
            return delegate.getNetworkTimeout();
        }
        
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return delegate.unwrap(iface);
        }
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return delegate.isWrapperFor(iface);
        }
    }
    
    // Classe pour les statistiques du pool
    public static class PoolStats {
        public final int activeConnections;
        public final int idleConnections;
        public final int totalConnections;
        public final int totalConnectionsCreated;
        
        public PoolStats(int active, int idle, int total, int created) {
            this.activeConnections = active;
            this.idleConnections = idle;
            this.totalConnections = total;
            this.totalConnectionsCreated = created;
        }
    }
}