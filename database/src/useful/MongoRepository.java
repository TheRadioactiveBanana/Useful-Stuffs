package useful;

import arc.util.Threads;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.model.changestream.*;
import org.bson.codecs.configuration.*;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;
import java.util.function.*;

public record MongoRepository<T>(MongoCollection<T> collection) {

    public static final CodecRegistry registry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    public MongoRepository(MongoDatabase database, String name, Class<T> type) {
        this(database.getCollection(name, type));
    }

    // region get

    public T get(String field, Object value) {
        return get(Filters.eq(field, value));
    }

    public T get(String field, Object value, T defaultDocument) {
        return get(Filters.eq(field, value), defaultDocument);
    }

    public T getAnd(String field1, Object value1, String field2, Object value2) {
        return get(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2)));
    }

    public T getAnd(String field1, Object value1, String field2, Object value2, T defaultDocument) {
        return get(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2)), defaultDocument);
    }

    public T getAnd(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
        return get(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)));
    }

    public T getAnd(String field1, Object value1, String field2, Object value2, String field3, Object value3, T defaultDocument) {
        return get(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)), defaultDocument);
    }

    public T getOr(String field1, Object value1, String field2, Object value2) {
        return get(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2)));
    }

    public T getOr(String field1, Object value1, String field2, Object value2, T defaultDocument) {
        return get(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2)), defaultDocument);
    }

    public T getOr(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
        return get(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)));
    }

    public T getOr(String field1, Object value1, String field2, Object value2, String field3, Object value3, T defaultDocument) {
        return get(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)), defaultDocument);
    }

    public T get(Bson filter) {
        return get(filter, null);
    }

    public T get(Bson filter, T defaultDocument) {
        var document = collection.find(filter).first();
        return document != null ? document : defaultDocument;
    }

    // endregion
    // region insert & replace

    public void insert(T document) {
        collection.insertOne(document);
    }

    public void replace(String field, Object value, T document) {
        replace(Filters.eq(field, value), document);
    }

    public void replace(String field, Object value, T document, boolean upsert) {
        replace(Filters.eq(field, value), document, upsert);
    }

    public void replaceAnd(String field1, Object value1, String field2, Object value2, T document) {
        replace(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2)), document);
    }

    public void replaceAnd(String field1, Object value1, String field2, Object value2, T document, boolean upsert) {
        replace(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2)), document, upsert);
    }

    public void replaceAnd(String field1, Object value1, String field2, Object value2, String field3, Object value3, T document) {
        replace(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)), document);
    }

    public void replaceAnd(String field1, Object value1, String field2, Object value2, String field3, Object value3, T document, boolean upsert) {
        replace(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)), document, upsert);
    }

    public void replaceOr(String field1, Object value1, String field2, Object value2, T document) {
        replace(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2)), document);
    }

    public void replaceOr(String field1, Object value1, String field2, Object value2, T document, boolean upsert) {
        replace(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2)), document, upsert);
    }

    public void replaceOr(String field1, Object value1, String field2, Object value2, String field3, Object value3, T document) {
        replace(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)), document);
    }

    public void replaceOr(String field1, Object value1, String field2, Object value2, String field3, Object value3, T document, boolean upsert) {
        replace(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)), document, upsert);
    }

    public void replace(Bson filter, T value) {
        replace(filter, value, true);
    }

    public void replace(Bson filter, T value, boolean upsert) {
        collection.replaceOne(filter, value, new ReplaceOptions().upsert(upsert));
    }

    // endregion
    // region remove

    public void delete(String field, Object value) {
        delete(Filters.eq(field, value));
    }

    public void deleteAnd(String field1, Object value1, String field2, Object value2) {
        delete(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2)));
    }

    public void deleteAnd(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
        delete(Filters.and(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)));
    }

    public void deleteOr(String field1, Object value1, String field2, Object value2) {
        delete(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2)));
    }

    public void deleteOr(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
        delete(Filters.or(Filters.eq(field1, value1), Filters.eq(field2, value2), Filters.eq(field3, value3)));
    }

    public void delete(Bson filter) {
        collection.deleteOne(filter);
    }

    // endregion
    // region watch

    public void watchBeforeChange(Consumer<T> cons) {
        watchBeforeChange(FullDocument.UPDATE_LOOKUP, cons);
    }

    public void watchBeforeChange(FullDocument fullDocument, Consumer<T> cons) {
        watch(fullDocument, stream -> {
            var before = stream.getFullDocumentBeforeChange();
            if (before == null) return;

            cons.accept(before);
        });
    }

    public void watchAfterChange(Consumer<T> cons) {
        watchAfterChange(FullDocument.UPDATE_LOOKUP, cons);
    }

    public void watchAfterChange(FullDocument fullDocument, Consumer<T> cons) {
        watch(fullDocument, stream -> {
            var after = stream.getFullDocument();
            if (after == null) return;

            cons.accept(after);
        });
    }

    public void watchBeforeAfterChange(BiConsumer<T, T> cons) {
        watchBeforeAfterChange(FullDocument.UPDATE_LOOKUP, cons);
    }

    public void watchBeforeAfterChange(FullDocument fullDocument, BiConsumer<T, T> cons) {
        watch(fullDocument, stream -> {
            var before = stream.getFullDocumentBeforeChange();
            if (before == null) return;

            var after = stream.getFullDocument();
            if (after == null) return;

            cons.accept(before, after);
        });
    }

    public void watch(OperationType type, Consumer<ChangeStreamDocument<T>> cons) {
        watch(FullDocument.UPDATE_LOOKUP, type, cons);
    }

    public void watch(FullDocument fullDocument, OperationType type, Consumer<ChangeStreamDocument<T>> cons) {
        watch(fullDocument, stream -> {
            if (stream.getOperationType() == type)
                cons.accept(stream);
        });
    }

    public void watch(Consumer<ChangeStreamDocument<T>> cons) {
        watch(FullDocument.UPDATE_LOOKUP, cons);
    }

    public void watch(FullDocument fullDocument, Consumer<ChangeStreamDocument<T>> cons) {
        Threads.daemon(() -> collection.watch(collection.getDocumentClass()).fullDocument(fullDocument).forEach(cons));
    }

    // endregion
    // region index

    public void ascendingIndex(String field) {
        ascendingIndex(field, new IndexOptions().name(field));
    }

    public void ascendingIndex(String field, long expireAfter) {
        ascendingIndex(field, new IndexOptions().name(field).expireAfter(expireAfter, TimeUnit.SECONDS));
    }

    public void ascendingIndex(String field, long expireAfter, TimeUnit unit) {
        ascendingIndex(field, new IndexOptions().name(field).expireAfter(expireAfter, unit));
    }

    public void ascendingIndex(String field, IndexOptions options) {
        index(Indexes.ascending(field), options);
    }

    public void descendingIndex(String field) {
        descendingIndex(field, new IndexOptions().name(field));
    }

    public void descendingIndex(String field, long expireAfter) {
        descendingIndex(field, new IndexOptions().name(field).expireAfter(expireAfter, TimeUnit.SECONDS));
    }

    public void descendingIndex(String field, long expireAfter, TimeUnit unit) {
        descendingIndex(field, new IndexOptions().name(field).expireAfter(expireAfter, unit));
    }

    public void descendingIndex(String field, IndexOptions options) {
        index(Indexes.descending(field), options);
    }

    public void index(Bson keys) {
        collection.createIndex(keys);
    }

    public void index(Bson keys, IndexOptions options) {
        collection.createIndex(keys, options);
    }

    // endregion
}