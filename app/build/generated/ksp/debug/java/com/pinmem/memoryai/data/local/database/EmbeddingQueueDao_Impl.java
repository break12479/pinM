package com.pinmem.memoryai.data.local.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pinmem.memoryai.data.model.EmbeddingQueue;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EmbeddingQueueDao_Impl implements EmbeddingQueueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EmbeddingQueue> __insertionAdapterOfEmbeddingQueue;

  private final EntityDeletionOrUpdateAdapter<EmbeddingQueue> __deletionAdapterOfEmbeddingQueue;

  private final EntityDeletionOrUpdateAdapter<EmbeddingQueue> __updateAdapterOfEmbeddingQueue;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMemoryId;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  private final SharedSQLiteStatement __preparedStmtOfClearFailedTasks;

  public EmbeddingQueueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEmbeddingQueue = new EntityInsertionAdapter<EmbeddingQueue>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `embedding_queue` (`id`,`memory_id`,`content`,`retry_count`,`created_at`,`last_attempt_at`,`error_message`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmbeddingQueue entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMemoryId());
        statement.bindString(3, entity.getContent());
        statement.bindLong(4, entity.getRetryCount());
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getLastAttemptAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLastAttemptAt());
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getErrorMessage());
        }
      }
    };
    this.__deletionAdapterOfEmbeddingQueue = new EntityDeletionOrUpdateAdapter<EmbeddingQueue>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `embedding_queue` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmbeddingQueue entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEmbeddingQueue = new EntityDeletionOrUpdateAdapter<EmbeddingQueue>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `embedding_queue` SET `id` = ?,`memory_id` = ?,`content` = ?,`retry_count` = ?,`created_at` = ?,`last_attempt_at` = ?,`error_message` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmbeddingQueue entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMemoryId());
        statement.bindString(3, entity.getContent());
        statement.bindLong(4, entity.getRetryCount());
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getLastAttemptAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLastAttemptAt());
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getErrorMessage());
        }
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByMemoryId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM embedding_queue WHERE memory_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM embedding_queue";
        return _query;
      }
    };
    this.__preparedStmtOfClearFailedTasks = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM embedding_queue WHERE retry_count >= ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final EmbeddingQueue queue, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEmbeddingQueue.insertAndReturnId(queue);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<EmbeddingQueue> queues,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEmbeddingQueue.insert(queues);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final EmbeddingQueue queue, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEmbeddingQueue.handle(queue);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final EmbeddingQueue queue, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEmbeddingQueue.handle(queue);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByMemoryId(final long memoryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMemoryId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, memoryId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByMemoryId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearFailedTasks(final int maxRetries,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearFailedTasks.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, maxRetries);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearFailedTasks.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EmbeddingQueue>> getAllPending() {
    final String _sql = "SELECT * FROM embedding_queue ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"embedding_queue"}, new Callable<List<EmbeddingQueue>>() {
      @Override
      @NonNull
      public List<EmbeddingQueue> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMemoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "memory_id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retry_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastAttemptAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_attempt_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final List<EmbeddingQueue> _result = new ArrayList<EmbeddingQueue>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmbeddingQueue _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMemoryId;
            _tmpMemoryId = _cursor.getLong(_cursorIndexOfMemoryId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastAttemptAt;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAt)) {
              _tmpLastAttemptAt = null;
            } else {
              _tmpLastAttemptAt = _cursor.getLong(_cursorIndexOfLastAttemptAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new EmbeddingQueue(_tmpId,_tmpMemoryId,_tmpContent,_tmpRetryCount,_tmpCreatedAt,_tmpLastAttemptAt,_tmpErrorMessage);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPendingTasks(final int limit,
      final Continuation<? super List<EmbeddingQueue>> $completion) {
    final String _sql = "SELECT * FROM embedding_queue ORDER BY created_at ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EmbeddingQueue>>() {
      @Override
      @NonNull
      public List<EmbeddingQueue> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMemoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "memory_id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retry_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastAttemptAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_attempt_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final List<EmbeddingQueue> _result = new ArrayList<EmbeddingQueue>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmbeddingQueue _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMemoryId;
            _tmpMemoryId = _cursor.getLong(_cursorIndexOfMemoryId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastAttemptAt;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAt)) {
              _tmpLastAttemptAt = null;
            } else {
              _tmpLastAttemptAt = _cursor.getLong(_cursorIndexOfLastAttemptAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new EmbeddingQueue(_tmpId,_tmpMemoryId,_tmpContent,_tmpRetryCount,_tmpCreatedAt,_tmpLastAttemptAt,_tmpErrorMessage);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRetryableTasks(final int maxRetries,
      final Continuation<? super List<EmbeddingQueue>> $completion) {
    final String _sql = "SELECT * FROM embedding_queue WHERE retry_count < ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, maxRetries);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EmbeddingQueue>>() {
      @Override
      @NonNull
      public List<EmbeddingQueue> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMemoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "memory_id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retry_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastAttemptAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_attempt_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final List<EmbeddingQueue> _result = new ArrayList<EmbeddingQueue>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmbeddingQueue _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMemoryId;
            _tmpMemoryId = _cursor.getLong(_cursorIndexOfMemoryId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastAttemptAt;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAt)) {
              _tmpLastAttemptAt = null;
            } else {
              _tmpLastAttemptAt = _cursor.getLong(_cursorIndexOfLastAttemptAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new EmbeddingQueue(_tmpId,_tmpMemoryId,_tmpContent,_tmpRetryCount,_tmpCreatedAt,_tmpLastAttemptAt,_tmpErrorMessage);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByMemoryId(final long memoryId,
      final Continuation<? super EmbeddingQueue> $completion) {
    final String _sql = "SELECT * FROM embedding_queue WHERE memory_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, memoryId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EmbeddingQueue>() {
      @Override
      @Nullable
      public EmbeddingQueue call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMemoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "memory_id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retry_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastAttemptAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_attempt_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final EmbeddingQueue _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMemoryId;
            _tmpMemoryId = _cursor.getLong(_cursorIndexOfMemoryId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastAttemptAt;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAt)) {
              _tmpLastAttemptAt = null;
            } else {
              _tmpLastAttemptAt = _cursor.getLong(_cursorIndexOfLastAttemptAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _result = new EmbeddingQueue(_tmpId,_tmpMemoryId,_tmpContent,_tmpRetryCount,_tmpCreatedAt,_tmpLastAttemptAt,_tmpErrorMessage);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM embedding_queue";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getFailedTasks(final int maxRetries,
      final Continuation<? super List<EmbeddingQueue>> $completion) {
    final String _sql = "SELECT * FROM embedding_queue WHERE retry_count >= ? ORDER BY last_attempt_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, maxRetries);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EmbeddingQueue>>() {
      @Override
      @NonNull
      public List<EmbeddingQueue> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMemoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "memory_id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retry_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastAttemptAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_attempt_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final List<EmbeddingQueue> _result = new ArrayList<EmbeddingQueue>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmbeddingQueue _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMemoryId;
            _tmpMemoryId = _cursor.getLong(_cursorIndexOfMemoryId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastAttemptAt;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAt)) {
              _tmpLastAttemptAt = null;
            } else {
              _tmpLastAttemptAt = _cursor.getLong(_cursorIndexOfLastAttemptAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new EmbeddingQueue(_tmpId,_tmpMemoryId,_tmpContent,_tmpRetryCount,_tmpCreatedAt,_tmpLastAttemptAt,_tmpErrorMessage);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByMemoryIds(final List<Long> memoryIds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM embedding_queue WHERE memory_id IN (");
        final int _inputSize = memoryIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : memoryIds) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
