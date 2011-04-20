package org.nds.dbdroid.remoting.webapp.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.util.ConfigHelper;

public class ScriptProcessor {

    private static final Log log = LogFactory.getLog(ScriptProcessor.class);

    private SessionFactory sessionFactory;
    private List<String> scripts;
    private List<String> arguments;

    public void runScripts() {
        for (final String script : scripts) {
            Session session = sessionFactory.openSession();
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    if (!connection.getAutoCommit()) {
                        connection.commit();
                        connection.setAutoCommit(false);
                    }
                    Statement statement = connection.createStatement();
                    InputStream stream = null;
                    try {
                        stream = ConfigHelper.getResourceAsStream(script);
                        List<String> queries = loadQueries(stream);
                        for (String query : queries) {
                            if (query.trim().length() > 0) {
                                log.debug(query);
                                statement.executeUpdate(query);
                            }
                        }
                    } finally {
                        try {
                            stream.close();
                        } catch (Exception exc) {
                        }
                    }
                    connection.commit();
                }
            });
            session.close();
        }
    }

    protected List<String> loadQueries(InputStream stream) {
        List<String> queries = new ArrayList<String>();

        BufferedReader reader = null;
        Reader in = null;
        try {
            in = new InputStreamReader(stream);
            reader = new BufferedReader(in);

            String sqlQuery = "";
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().length() <= 0) {
                    continue;
                }
                // Skip comments
                if (line.startsWith("//") || line.startsWith("#") || line.startsWith("--")) {
                    continue;
                }

                sqlQuery += " " + line;

                if (sqlQuery.endsWith("/")) { // complete command
                    sqlQuery = sqlQuery.replace('/', ' '); // Remove the '/' since jdbc complains
                    queries.add(replaceArguments(sqlQuery, arguments));
                    sqlQuery = "";
                } else if (sqlQuery.contains(";")) { // One or several complete query(ies)
                    String[] q = sqlQuery.split(";");
                    // Loop on different queries
                    for (int i = 0; i < (q.length - 1); i++) {
                        queries.add(replaceArguments(q[i], arguments));
                    }
                    // Check if the line ends with
                    if (sqlQuery.endsWith(";")) {
                        queries.add(replaceArguments(q[q.length - 1], arguments));
                        sqlQuery = "";
                    } else {
                        sqlQuery = q[q.length - 1];
                    }
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception exc) {
            }
            try {
                reader.close();
            } catch (Exception exc) {
            }
        }

        return queries;
    }

    private static String replaceArguments(String sqlQuery, List<String> args) {
        String query = sqlQuery;
        if (args != null) {
            for (int a = 0; a < args.size(); a++) {
                query = query.replace("&" + (a + 1), args.get(a));
            }
        }

        return query;
    }

    /**
     * @param sessionFactory
     *            the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @param scripts
     *            the scripts to set
     */
    public void setScripts(List<String> scripts) {
        this.scripts = scripts;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
