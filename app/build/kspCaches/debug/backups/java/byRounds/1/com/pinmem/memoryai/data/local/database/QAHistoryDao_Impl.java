package com.pinmem.memoryai.data.local.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pinmem.memoryai.data.model.QAHistory;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class QAHistoryDao_Impl implements QAHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QAHistory> __insertionAdapterOfQAHistory;

  private final EntityDeletionOrUpdateAdapter<QAHistory> __deletionAdapterOfQAHistory;

  private final EntityDeletionOrUpdateAdapter<QAHistory> __updateAdapterOfQAHistory;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFeedback;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public QAHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQAHistory = new EntityInsertionAdapter<QAHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `qa_history` (`id`,`question`,`answer`,`referenced_memory_ids`,`created_at`,`feedback`,`model_used`,`tokens_used`,`latency_ms`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QAHistory entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getQuestion());
        statement.bindString(3, entity.getAnswer());
        statement.bindString(4, entity.getReferencedMemoryIds());
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getFeedback() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getFeedback());
        }
        if (entity.getModelUsed() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getModelUsed());
        }
        if (entity.getTokensUsed() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getTokensUsed());
        }
        if (entity.getLatencyMs() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getLatencyMs());
        }
      }
    };
    this.__deletionAdapterOfQAHistory = new EntityDeletionOrUpdateAdapter<QAHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `qa_history` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QAHistory entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfQAHistory = new EntityDeletionOrUpdateAdapter<QAHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `qa_history` SET `id` = ?,`question` = ?,`answer` = ?,`referenced_memory_ids` = ?,`created_at` = ?,`feedback` = ?,`model_used` = ?,`tokens_used` = ?,`latency_ms` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QAHistory entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getQuestion());
        statement.bindString(3, entity.getAnswer());
        statement.bindString(4, entity.getReferencedMemoryIds());
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getFeedback() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getFeedback());
        }
        if (entity.getModelUsed() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getModelUsed());
        }
        if (entity.getTokensUsed() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getTokensUsed());
        }
        if (entity.getLatencyMs() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getLatencyMs());
        }
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateFeedback = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE qa_history SET feedback = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM qa_history";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final QAHistory history, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfQAHistory.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final QAHistory history, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfQAHistory.handle(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final QAHistory history, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfQAHistory.handle(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFeedback(final long id, final int feedback,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFeedback.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, feedback);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUpdateFeedback.release(_stmt);
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
  public Flow<List<QAHistory>> getHistory(final int limit) {
    final String _sql = "SELECT * FROM qa_history ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"qa_history"}, new Callable<List<QAHistory>>() {
      @Override
      @NonNull
      public List<QAHistory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfReferencedMemoryIds = CursorUtil.getColumnIndexOrThrow(_cursor, "referenced_memory_ids");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfFeedback = CursorUtil.getColumnIndexOrThrow(_cursor, "feedback");
          final int _cursorIndexOfModelUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "model_used");
          final int _cursorIndexOfTokensUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "tokens_used");
          final int _cursorIndexOfLatencyMs = CursorUtil.getColumnIndexOrThrow(_cursor, "latency_ms");
          final List<QAHistory> _result = new ArrayList<QAHistory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QAHistory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpAnswer;
            _tmpAnswer = _cursor.getString(_cursorIndexOfAnswer);
            final String _tmpReferencedMemoryIds;
            _tmpReferencedMemoryIds = _cursor.getString(_cursorIndexOfReferencedMemoryIds);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Integer _tmpFeedback;
            if (_cursor.isNull(_cursorIndexOfFeedback)) {
              _tmpFeedback = null;
            } else {
              _tmpFeedback = _cursor.getInt(_cursorIndexOfFeedback);
            }
            final String _tmpModelUsed;
            if (_cursor.isNull(_cursorIndexOfModelUsed)) {
              _tmpModelUsed = null;
            } else {
              _tmpModelUsed = _cursor.getString(_cursorIndexOfModelUsed);
            }
            final Integer _tmpTokensUsed;
            if (_cursor.isNull(_cursorIndexOfTokensUsed)) {
              _tmpTokensUsed = null;
            } else {
              _tmpTokensUsed = _cursor.getInt(_cursorIndexOfTokensUsed);
            }
            final Long _tmpLatencyMs;
            if (_cursor.isNull(_cursorIndexOfLatencyMs)) {
              _tmpLatencyMs = null;
            } else {
              _tmpLatencyMs = _cursor.getLong(_cursorIndexOfLatencyMs);
            }
            _item = new QAHistory(_tmpId,_tmpQuestion,_tmpAnswer,_tmpReferencedMemoryIds,_tmpCreatedAt,_tmpFeedback,_tmpModelUsed,_tmpTokensUsed,_tmpLatencyMs);
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
  public Object getHistoryOnce(final int limit,
      final Continuation<? super List<QAHistory>> $completion) {
    final String _sql = "SELECT * FROM qa_history ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QAHistory>>() {
      @Override
      @NonNull
      public List<QAHistory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfReferencedMemoryIds = CursorUtil.getColumnIndexOrThrow(_cursor, "referenced_memory_ids");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfFeedback = CursorUtil.getColumnIndexOrThrow(_cursor, "feedback");
          final int _cursorIndexOfModelUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "model_used");
          final int _cursorIndexOfTokensUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "tokens_used");
          final int _cursorIndexOfLatencyMs = CursorUtil.getColumnIndexOrThrow(_cursor, "latency_ms");
          final List<QAHistory> _result = new ArrayList<QAHistory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QAHistory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpAnswer;
            _tmpAnswer = _cursor.getString(_cursorIndexOfAnswer);
            final String _tmpReferencedMemoryIds;
            _tmpReferencedMemoryIds = _cursor.getString(_cursorIndexOfReferencedMemoryIds);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Integer _tmpFeedback;
            if (_cursor.isNull(_cursorIndexOfFeedback)) {
              _tmpFeedback = null;
            } else {
              _tmpFeedback = _cursor.getInt(_cursorIndexOfFeedback);
            }
            final String _tmpModelUsed;
            if (_cursor.isNull(_cursorIndexOfModelUsed)) {
              _tmpModelUsed = null;
            } else {
              _tmpModelUsed = _cursor.getString(_cursorIndexOfModelUsed);
            }
            final Integer _tmpTokensUsed;
            if (_cursor.isNull(_cursorIndexOfTokensUsed)) {
              _tmpTokensUsed = null;
            } else {
              _tmpTokensUsed = _cursor.getInt(_cursorIndexOfTokensUsed);
            }
            final Long _tmpLatencyMs;
            if (_cursor.isNull(_cursorIndexOfLatencyMs)) {
              _tmpLatencyMs = null;
            } else {
              _tmpLatencyMs = _cursor.getLong(_cursorIndexOfLatencyMs);
            }
            _item = new QAHistory(_tmpId,_tmpQuestion,_tmpAnswer,_tmpReferencedMemoryIds,_tmpCreatedAt,_tmpFeedback,_tmpModelUsed,_tmpTokensUsed,_tmpLatencyMs);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
