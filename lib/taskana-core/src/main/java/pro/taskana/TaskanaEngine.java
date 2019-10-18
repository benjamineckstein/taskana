package pro.taskana;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.history.HistoryEventProducer;

/**
 * The TaskanaEngine represents an overall set of all needed services.
 */
public interface TaskanaEngine {

    /**
     * The TaskService can be used for operations on all Tasks.
     *
     * @return the TaskService
     */
    TaskService getTaskService();

    /**
     * The TaskMonitorService can be used for monitoring Tasks.
     *
     * @return the TaskMonitorService
     */
    TaskMonitorService getTaskMonitorService();

    /**
     * The WorkbasketService can be used for operations on all Workbaskets.
     *
     * @return the TaskService
     */
    WorkbasketService getWorkbasketService();

    /**
     * The ClassificationService can be used for operations on all Categories.
     *
     * @return the TaskService
     */
    ClassificationService getClassificationService();

    /**
     * The JobService can be user for all job operations.
     *
     * @return the JobService
     */
    JobService getJobService();

    /**
     * The Taskana configuration.
     *
     * @return the TaskanaConfiguration
     */
    TaskanaEngineConfiguration getConfiguration();

    /**
     * Checks if the history plugin is enabled.
     *
     * @return true if the history is enabled. Otherwise false.
     */
    boolean isHistoryEnabled();

    /**
     * sets the connection management mode.
     *
     * @param mode
     *            the connection management mode Valid values are:
     *            <ul>
     *            <li>PARTICIPATE - taskana participates in global transaction. This is the default mode.</li>
     *            <li>AUTOCOMMIT - taskana commits each API call separately</li>
     *            <li>EXPLICIT - commit processing is managed explicitly by the client</li>
     *            </ul>
     */
    void setConnectionManagementMode(ConnectionManagementMode mode);

    /**
     * Set the connection to be used by taskana in mode CONNECTION_MANAGED_EXTERNALLY. If this Api is called, taskana
     * uses the connection passed by the client for all subsequent API calls until the client resets this connection.
     * Control over commit and rollback of the connection is the responsibility of the client. In order to close the
     * connection, closeConnection() or setConnection(null) has to be called.
     *
     * @param connection
     *            - The java.sql.Connection that is controlled by the client
     * @throws SQLException
     *             if a database access error occurs
     */
    void setConnection(java.sql.Connection connection) throws SQLException;

    /**
     * Closes the client's connection, sets it to null and switches to mode PARTICIPATE. Only applicable in mode
     * EXPLICIT. Has the same effect as setConnection(null).
     */
    void closeConnection();

    /**
     * check whether the current user is member of one of the roles specified.
     *
     * @param roles
     *            The roles that are checked for membership of the current user
     * @return true if the current user is a member of at least one of the specified groups
     */
    boolean isUserInRole(TaskanaRole... roles);

    /**
     * Checks whether current user is member of any of the specified roles.
     *
     * @param roles
     *            The roles that are checked for membership of the current user
     * @throws NotAuthorizedException
     *             If the current user is not member of any specified role
     */
    void checkRoleMembership(TaskanaRole... roles) throws NotAuthorizedException;

    /**
     * Connection management mode. Controls the connection handling of taskana
     * <ul>
     * <li>PARTICIPATE - taskana participates in global transaction. This is the default mode</li>
     * <li>AUTOCOMMIT - taskana commits each API call separately</li>
     * <li>EXPLICIT - commit processing is managed explicitly by the client</li>
     * </ul>
     */
    enum ConnectionManagementMode {
        PARTICIPATE,
        AUTOCOMMIT,
        EXPLICIT
    }

    /**
     * FOR INTERNAL USE ONLY.
     *
     * Contains all actions which are necessary within taskana.
     */
    interface Internal {

        /**
         * Opens the connection to the database. Has to be called at the begin of each Api call that accesses the database
         */
        void openConnection();

        /**
         * Returns the database connection into the pool. In the case of nested calls, simply pops the latest session from
         * the session stack. Closes the connection if the session stack is empty. In mode AUTOCOMMIT commits before the
         * connection is closed. To be called at the end of each Api call that accesses the database
         */
        void returnConnection();

        /**
         * Initializes the SqlSessionManager.
         */
        void initSqlSession();

        /**
         * Returns true if the given domain does exist in the configuration.
         *
         * @param domain
         *            the domain specified in the configuration
         * @return <code>true</code> if the domain exists
         */
        boolean domainExists(String domain);

        /**
         * retrieve the SqlSession used by taskana.
         *
         * @return the myBatis SqlSession object used by taskana
         */
        SqlSession getSqlSession();

        /**
         * Retrieve TaskanaEngine.
         * @return The nested TaskanaEngine.
         */
        TaskanaEngine getEngine();

        /**
         * Retrieve HistoryEventProducer.
         *
         * @return the HistoryEventProducer instance.
         */
        HistoryEventProducer getHistoryEventProducer();

    }

}
