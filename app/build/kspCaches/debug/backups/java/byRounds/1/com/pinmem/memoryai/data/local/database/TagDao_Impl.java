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
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pinmem.memoryai.data.model.Tag;
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
public final class TagDao_Impl implements TagDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Tag> __insertionAdapterOfTag;

  private final EntityDeletionOrUpdateAdapter<Tag> __deletionAdapterOfTag;

  private final EntityDeletionOrUpdateAdapter<Tag> __updateAdapterOfTag;

  private final SharedSQLiteStatement __preparedStmtOfIncrementUsage;

  public TagDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTag = new EntityInsertionAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `tags` (`id`,`name`,`embedding`,`embedding_model`,`usage_count`,`created_at`,`updated_at`,`is_preferred`,`alias_of`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getEmbedding() == null) {
          statement.bindNull(3);
        } else {
          statement.bindBlob(3, entity.getEmbedding());
        }
        if (entity.getEmbeddingModel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getEmbeddingModel());
        }
        statement.bindLong(5, entity.getUsageCount());
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
        statement.bindLong(8, entity.isPreferred());
        if (entity.getAliasOf() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAliasOf());
        }
      }
    };
    this.__deletionAdapterOfTag = new EntityDeletionOrUpdateAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tags` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTag = new EntityDeletionOrUpdateAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tags` SET `id` = ?,`name` = ?,`embedding` = ?,`embedding_model` = ?,`usage_count` = ?,`created_at` = ?,`updated_at` = ?,`is_preferred` = ?,`alias_of` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getEmbedding() == null) {
          statement.bindNull(3);
        } else {
          statement.bindBlob(3, entity.getEmbedding());
        }
        if (entity.getEmbeddingModel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getEmbeddingModel());
        }
        statement.bindLong(5, entity.getUsageCount());
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
        statement.bindLong(8, entity.isPreferred());
        if (entity.getAliasOf() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAliasOf());
        }
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfIncrementUsage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tags SET usage_count = usage_count + 1, updated_at = ? WHERE name = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Tag tag, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTag.insertAndReturnId(tag);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTag.handle(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTag.handle(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementUsage(final String name, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementUsage.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 2;
        _stmt.bindString(_argIndex, name);
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
          __preparedStmtOfIncrementUsage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Tag>> getAllTags() {
    final String _sql = "SELECT * FROM tags ORDER BY usage_count DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tags"}, new Callable<List<Tag>>() {
      @Override
      @NonNull
      public List<Tag> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usage_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsPreferred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_preferred");
          final int _cursorIndexOfAliasOf = CursorUtil.getColumnIndexOrThrow(_cursor, "alias_of");
          final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tag _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final byte[] _tmpEmbedding;
            if (_cursor.isNull(_cursorIndexOfEmbedding)) {
              _tmpEmbedding = null;
            } else {
              _tmpEmbedding = _cursor.getBlob(_cursorIndexOfEmbedding);
            }
            final String _tmpEmbeddingModel;
            if (_cursor.isNull(_cursorIndexOfEmbeddingModel)) {
              _tmpEmbeddingModel = null;
            } else {
              _tmpEmbeddingModel = _cursor.getString(_cursorIndexOfEmbeddingModel);
            }
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final int _tmpIsPreferred;
            _tmpIsPreferred = _cursor.getInt(_cursorIndexOfIsPreferred);
            final String _tmpAliasOf;
            if (_cursor.isNull(_cursorIndexOfAliasOf)) {
              _tmpAliasOf = null;
            } else {
              _tmpAliasOf = _cursor.getString(_cursorIndexOfAliasOf);
            }
            _item = new Tag(_tmpId,_tmpName,_tmpEmbedding,_tmpEmbeddingModel,_tmpUsageCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsPreferred,_tmpAliasOf);
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
  public Object getAllTagsOnce(final Continuation<? super List<Tag>> $completion) {
    final String _sql = "SELECT * FROM tags ORDER BY usage_count DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Tag>>() {
      @Override
      @NonNull
      public List<Tag> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usage_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsPreferred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_preferred");
          final int _cursorIndexOfAliasOf = CursorUtil.getColumnIndexOrThrow(_cursor, "alias_of");
          final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tag _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final byte[] _tmpEmbedding;
            if (_cursor.isNull(_cursorIndexOfEmbedding)) {
              _tmpEmbedding = null;
            } else {
              _tmpEmbedding = _cursor.getBlob(_cursorIndexOfEmbedding);
            }
            final String _tmpEmbeddingModel;
            if (_cursor.isNull(_cursorIndexOfEmbeddingModel)) {
              _tmpEmbeddingModel = null;
            } else {
              _tmpEmbeddingModel = _cursor.getString(_cursorIndexOfEmbeddingModel);
            }
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final int _tmpIsPreferred;
            _tmpIsPreferred = _cursor.getInt(_cursorIndexOfIsPreferred);
            final String _tmpAliasOf;
            if (_cursor.isNull(_cursorIndexOfAliasOf)) {
              _tmpAliasOf = null;
            } else {
              _tmpAliasOf = _cursor.getString(_cursorIndexOfAliasOf);
            }
            _item = new Tag(_tmpId,_tmpName,_tmpEmbedding,_tmpEmbeddingModel,_tmpUsageCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsPreferred,_tmpAliasOf);
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
  public Object getTagByName(final String name, final Continuation<? super Tag> $completion) {
    final String _sql = "SELECT * FROM tags WHERE name = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Tag>() {
      @Override
      @Nullable
      public Tag call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usage_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsPreferred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_preferred");
          final int _cursorIndexOfAliasOf = CursorUtil.getColumnIndexOrThrow(_cursor, "alias_of");
          final Tag _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final byte[] _tmpEmbedding;
            if (_cursor.isNull(_cursorIndexOfEmbedding)) {
              _tmpEmbedding = null;
            } else {
              _tmpEmbedding = _cursor.getBlob(_cursorIndexOfEmbedding);
            }
            final String _tmpEmbeddingModel;
            if (_cursor.isNull(_cursorIndexOfEmbeddingModel)) {
              _tmpEmbeddingModel = null;
            } else {
              _tmpEmbeddingModel = _cursor.getString(_cursorIndexOfEmbeddingModel);
            }
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final int _tmpIsPreferred;
            _tmpIsPreferred = _cursor.getInt(_cursorIndexOfIsPreferred);
            final String _tmpAliasOf;
            if (_cursor.isNull(_cursorIndexOfAliasOf)) {
              _tmpAliasOf = null;
            } else {
              _tmpAliasOf = _cursor.getString(_cursorIndexOfAliasOf);
            }
            _result = new Tag(_tmpId,_tmpName,_tmpEmbedding,_tmpEmbeddingModel,_tmpUsageCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsPreferred,_tmpAliasOf);
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
  public Object searchTags(final String query, final Continuation<? super List<Tag>> $completion) {
    final String _sql = "SELECT * FROM tags WHERE name LIKE ? ORDER BY usage_count DESC LIMIT 10";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Tag>>() {
      @Override
      @NonNull
      public List<Tag> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usage_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsPreferred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_preferred");
          final int _cursorIndexOfAliasOf = CursorUtil.getColumnIndexOrThrow(_cursor, "alias_of");
          final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tag _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final byte[] _tmpEmbedding;
            if (_cursor.isNull(_cursorIndexOfEmbedding)) {
              _tmpEmbedding = null;
            } else {
              _tmpEmbedding = _cursor.getBlob(_cursorIndexOfEmbedding);
            }
            final String _tmpEmbeddingModel;
            if (_cursor.isNull(_cursorIndexOfEmbeddingModel)) {
              _tmpEmbeddingModel = null;
            } else {
              _tmpEmbeddingModel = _cursor.getString(_cursorIndexOfEmbeddingModel);
            }
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final int _tmpIsPreferred;
            _tmpIsPreferred = _cursor.getInt(_cursorIndexOfIsPreferred);
            final String _tmpAliasOf;
            if (_cursor.isNull(_cursorIndexOfAliasOf)) {
              _tmpAliasOf = null;
            } else {
              _tmpAliasOf = _cursor.getString(_cursorIndexOfAliasOf);
            }
            _item = new Tag(_tmpId,_tmpName,_tmpEmbedding,_tmpEmbeddingModel,_tmpUsageCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsPreferred,_tmpAliasOf);
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
  public Object getTopTags(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT name FROM tags ORDER BY usage_count DESC LIMIT 50";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
