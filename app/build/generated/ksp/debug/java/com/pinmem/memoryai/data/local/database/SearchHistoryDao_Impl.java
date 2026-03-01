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
import com.pinmem.memoryai.data.model.SearchHistory;
import java.lang.Class;
import java.lang.Exception;
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
public final class SearchHistoryDao_Impl implements SearchHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SearchHistory> __insertionAdapterOfSearchHistory;

  private final EntityDeletionOrUpdateAdapter<SearchHistory> __deletionAdapterOfSearchHistory;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public SearchHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSearchHistory = new EntityInsertionAdapter<SearchHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `search_history` (`id`,`query`,`result_count`,`created_at`,`search_type`,`filter_tags`,`filter_category`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SearchHistory entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getQuery());
        statement.bindLong(3, entity.getResultCount());
        statement.bindLong(4, entity.getCreatedAt());
        statement.bindString(5, entity.getSearchType());
        if (entity.getFilterTags() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFilterTags());
        }
        if (entity.getFilterCategory() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFilterCategory());
        }
      }
    };
    this.__deletionAdapterOfSearchHistory = new EntityDeletionOrUpdateAdapter<SearchHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `search_history` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SearchHistory entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_history WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_history";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_history WHERE created_at < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SearchHistory history, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSearchHistory.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<SearchHistory> histories,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSearchHistory.insert(histories);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final SearchHistory history, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSearchHistory.handle(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeleteById.release(_stmt);
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
  public Object deleteOlderThan(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SearchHistory>> getRecentHistory(final int limit) {
    final String _sql = "SELECT * FROM search_history ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"search_history"}, new Callable<List<SearchHistory>>() {
      @Override
      @NonNull
      public List<SearchHistory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuery = CursorUtil.getColumnIndexOrThrow(_cursor, "query");
          final int _cursorIndexOfResultCount = CursorUtil.getColumnIndexOrThrow(_cursor, "result_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfSearchType = CursorUtil.getColumnIndexOrThrow(_cursor, "search_type");
          final int _cursorIndexOfFilterTags = CursorUtil.getColumnIndexOrThrow(_cursor, "filter_tags");
          final int _cursorIndexOfFilterCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "filter_category");
          final List<SearchHistory> _result = new ArrayList<SearchHistory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SearchHistory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpQuery;
            _tmpQuery = _cursor.getString(_cursorIndexOfQuery);
            final int _tmpResultCount;
            _tmpResultCount = _cursor.getInt(_cursorIndexOfResultCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpSearchType;
            _tmpSearchType = _cursor.getString(_cursorIndexOfSearchType);
            final String _tmpFilterTags;
            if (_cursor.isNull(_cursorIndexOfFilterTags)) {
              _tmpFilterTags = null;
            } else {
              _tmpFilterTags = _cursor.getString(_cursorIndexOfFilterTags);
            }
            final String _tmpFilterCategory;
            if (_cursor.isNull(_cursorIndexOfFilterCategory)) {
              _tmpFilterCategory = null;
            } else {
              _tmpFilterCategory = _cursor.getString(_cursorIndexOfFilterCategory);
            }
            _item = new SearchHistory(_tmpId,_tmpQuery,_tmpResultCount,_tmpCreatedAt,_tmpSearchType,_tmpFilterTags,_tmpFilterCategory);
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
  public Object getRecentHistoryOnce(final int limit,
      final Continuation<? super List<SearchHistory>> $completion) {
    final String _sql = "SELECT * FROM search_history ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SearchHistory>>() {
      @Override
      @NonNull
      public List<SearchHistory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuery = CursorUtil.getColumnIndexOrThrow(_cursor, "query");
          final int _cursorIndexOfResultCount = CursorUtil.getColumnIndexOrThrow(_cursor, "result_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfSearchType = CursorUtil.getColumnIndexOrThrow(_cursor, "search_type");
          final int _cursorIndexOfFilterTags = CursorUtil.getColumnIndexOrThrow(_cursor, "filter_tags");
          final int _cursorIndexOfFilterCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "filter_category");
          final List<SearchHistory> _result = new ArrayList<SearchHistory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SearchHistory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpQuery;
            _tmpQuery = _cursor.getString(_cursorIndexOfQuery);
            final int _tmpResultCount;
            _tmpResultCount = _cursor.getInt(_cursorIndexOfResultCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpSearchType;
            _tmpSearchType = _cursor.getString(_cursorIndexOfSearchType);
            final String _tmpFilterTags;
            if (_cursor.isNull(_cursorIndexOfFilterTags)) {
              _tmpFilterTags = null;
            } else {
              _tmpFilterTags = _cursor.getString(_cursorIndexOfFilterTags);
            }
            final String _tmpFilterCategory;
            if (_cursor.isNull(_cursorIndexOfFilterCategory)) {
              _tmpFilterCategory = null;
            } else {
              _tmpFilterCategory = _cursor.getString(_cursorIndexOfFilterCategory);
            }
            _item = new SearchHistory(_tmpId,_tmpQuery,_tmpResultCount,_tmpCreatedAt,_tmpSearchType,_tmpFilterTags,_tmpFilterCategory);
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
  public Object searchHistory(final String query,
      final Continuation<? super List<SearchHistory>> $completion) {
    final String _sql = "SELECT * FROM search_history WHERE query LIKE ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SearchHistory>>() {
      @Override
      @NonNull
      public List<SearchHistory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuery = CursorUtil.getColumnIndexOrThrow(_cursor, "query");
          final int _cursorIndexOfResultCount = CursorUtil.getColumnIndexOrThrow(_cursor, "result_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfSearchType = CursorUtil.getColumnIndexOrThrow(_cursor, "search_type");
          final int _cursorIndexOfFilterTags = CursorUtil.getColumnIndexOrThrow(_cursor, "filter_tags");
          final int _cursorIndexOfFilterCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "filter_category");
          final List<SearchHistory> _result = new ArrayList<SearchHistory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SearchHistory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpQuery;
            _tmpQuery = _cursor.getString(_cursorIndexOfQuery);
            final int _tmpResultCount;
            _tmpResultCount = _cursor.getInt(_cursorIndexOfResultCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpSearchType;
            _tmpSearchType = _cursor.getString(_cursorIndexOfSearchType);
            final String _tmpFilterTags;
            if (_cursor.isNull(_cursorIndexOfFilterTags)) {
              _tmpFilterTags = null;
            } else {
              _tmpFilterTags = _cursor.getString(_cursorIndexOfFilterTags);
            }
            final String _tmpFilterCategory;
            if (_cursor.isNull(_cursorIndexOfFilterCategory)) {
              _tmpFilterCategory = null;
            } else {
              _tmpFilterCategory = _cursor.getString(_cursorIndexOfFilterCategory);
            }
            _item = new SearchHistory(_tmpId,_tmpQuery,_tmpResultCount,_tmpCreatedAt,_tmpSearchType,_tmpFilterTags,_tmpFilterCategory);
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
  public Object getUniqueQueries(final int limit,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT query FROM search_history ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
