import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tasos on 29/11/2017.
 */
public class TransactionTests {

    @Before
    public void deleteAllTransactions() {
        Repository.deleteAllTransactions();
    }

    @Test
    public void transactionWorksAsExpected() {
        BatchStatement batch;

        Instant nowFirstBatch = Instant.now();

        //create the transaction and add them to the batch
        batch = createTransactionsBatchStatement(nowFirstBatch);

        //execute the batch & assert that it has been applied
        ResultSet resultSet = Repository.session.execute(batch);
        Assert.assertEquals(true, resultSet.wasApplied());

        batch = createTransactionsBatchStatement(Instant.now());

        //now try to add them again - assert that they fail
        resultSet = Repository.session.execute(batch);
        Assert.assertEquals(false, resultSet.wasApplied());

        //now try to retrieve the data
        List<Transaction> transactions = Repository.getTransactions();

        //find the records with time other than the initial one
        long recordsWithOtherThanInitialTime = transactions.stream().filter(t -> t.getTime().toEpochMilli() != nowFirstBatch.toEpochMilli()).count();

        //assert that there is no record with modified time other than the initially provided
        Assert.assertEquals(0, recordsWithOtherThanInitialTime);
    }

    @Test(expected = InvalidQueryException.class)
    public void batchIsFailedWithSpannedQueriesOnMultiplePartitions() {
        BatchStatement batch;

        Instant nowFirstBatch = Instant.now();

        //create the transaction and add them to the batch
        batch = createSpannedTransactionsBatchStatement(nowFirstBatch);

        //execute the batch - it should throw an InvalidQueryException because it spans on multiple partitions with
        //having IF NOT EXISTS statements
        Repository.session.execute(batch);
    }

    private BatchStatement createTransactionsBatchStatement(Instant now) {
        BatchStatement batch = new BatchStatement();

        List<Transaction> transactions = new ArrayList<>();

        //create the transaction and add them to the batch
        transactions.add(new Transaction("29-11-2017","1", now));
        transactions.add(new Transaction("29-11-2017","2", now));
        transactions.add(new Transaction("29-11-2017","3", now));

        Collections.shuffle(transactions);

        transactions.forEach(t -> batch.add(Repository.getInsertTransactionStatement(t)));

        return batch;
    }

    private BatchStatement createSpannedTransactionsBatchStatement(Instant now) {
        BatchStatement batch = new BatchStatement();

        List<Transaction> transactions = new ArrayList<>();

        //create the transaction and add them to the batch
        transactions.add(new Transaction("28-11-2017","1", now));
        transactions.add(new Transaction("29-11-2017","2", now));
        transactions.add(new Transaction("30-11-2017","3", now));

        Collections.shuffle(transactions);

        transactions.forEach(t -> batch.add(Repository.getInsertTransactionStatement(t)));

        return batch;
    }
}
