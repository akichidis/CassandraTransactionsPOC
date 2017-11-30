import com.datastax.driver.core.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tasos on 29/11/2017.
 */
public class Repository {
    public static Session session = createSession();

    private static PreparedStatement INSERT_TRANSACTION =
            session.prepare("INSERT INTO TRANSACTIONS (DATE, TRANSACTION_ID, MODIFIED) VALUES (:date, :transactionId, :modified) IF NOT EXISTS");

    private static PreparedStatement SELECT_TRANSACTIONS =
            session.prepare("SELECT * FROM TRANSACTIONS");

    private static Session createSession() {
        Cluster cluster = Cluster.builder().addContactPoint("localhost").build();

        Session session = cluster.connect("transactions_poc");

        return session;
    }

    public static Statement getInsertTransactionStatement(Transaction transaction) {
        BoundStatement statement = new BoundStatement(INSERT_TRANSACTION);

        statement.setString("transactionId", transaction.getTxId())
                 .setTime("modified", transaction.getTime().toEpochMilli())
                 .setString("date", transaction.getDate());

        return statement;
    }

    public static List<Transaction> getTransactions() {
        BoundStatement statement = new BoundStatement(SELECT_TRANSACTIONS);

        ResultSet resultSet = session.execute(statement);

        return resultSet.all().stream().map(Repository::convertToTransaction).collect(Collectors.toList());
    }

    public static void deleteAllTransactions() {
        session.execute("TRUNCATE TRANSACTIONS");
    }

    private static Transaction convertToTransaction(Row row) {
        return new Transaction(row.getString("date"), row.getString("TRANSACTION_ID"), Instant.ofEpochMilli(row.getTime("MODIFIED")));
    }
}
