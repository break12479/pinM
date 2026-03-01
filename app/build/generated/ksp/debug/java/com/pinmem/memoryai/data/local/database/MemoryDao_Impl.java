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
import com.pinmem.memoryai.data.model.Memory;
import java.lang.Class;
import java.lang.Double;
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
public final class MemoryDao_Impl implements MemoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Memory> __insertionAdapterOfMemory;

  private final EntityDeletionOrUpdateAdapter<Memory> __deletionAdapterOfMemory;

  private final EntityDeletionOrUpdateAdapter<Memory> __updateAdapterOfMemory;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAIStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateEmbedding;

  public MemoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMemory = new EntityInsertionAdapter<Memory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `memories` (`id`,`content`,`media_type`,`media_path`,`media_description`,`created_at`,`updated_at`,`location_lat`,`location_lng`,`location_address`,`scene_category`,`type_category`,`tags`,`embedding`,`embedding_model`,`is_deleted`,`deleted_at`,`ai_processed`,`embedding_pending`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Memory entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getMediaType());
        if (entity.getMediaPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMediaPath());
        }
        if (entity.getMediaDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMediaDescription());
        }
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
        if (entity.getLocationLat() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLocationLat());
        }
        if (entity.getLocationLng() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getLocationLng());
        }
        if (entity.getLocationAddress() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getLocationAddress());
        }
        statement.bindString(11, entity.getSceneCategory());
        statement.bindString(12, entity.getTypeCategory());
        statement.bindString(13, entity.getTagsJson());
        if (entity.getEmbedding() == null) {
          statement.bindNull(14);
        } else {
          statement.bindBlob(14, entity.getEmbedding());
        }
        if (entity.getEmbeddingModel() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getEmbeddingModel());
        }
        statement.bindLong(16, entity.isDeleted());
        if (entity.getDeletedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getDeletedAt());
        }
        statement.bindLong(18, entity.getAiProcessed());
        statement.bindLong(19, entity.getEmbeddingPending());
      }
    };
    this.__deletionAdapterOfMemory = new EntityDeletionOrUpdateAdapter<Memory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `memories` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Memory entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMemory = new EntityDeletionOrUpdateAdapter<Memory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `memories` SET `id` = ?,`content` = ?,`media_type` = ?,`media_path` = ?,`media_description` = ?,`created_at` = ?,`updated_at` = ?,`location_lat` = ?,`location_lng` = ?,`location_address` = ?,`scene_category` = ?,`type_category` = ?,`tags` = ?,`embedding` = ?,`embedding_model` = ?,`is_deleted` = ?,`deleted_at` = ?,`ai_processed` = ?,`embedding_pending` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Memory entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getMediaType());
        if (entity.getMediaPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMediaPath());
        }
        if (entity.getMediaDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMediaDescription());
        }
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
        if (entity.getLocationLat() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLocationLat());
        }
        if (entity.getLocationLng() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getLocationLng());
        }
        if (entity.getLocationAddress() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getLocationAddress());
        }
        statement.bindString(11, entity.getSceneCategory());
        statement.bindString(12, entity.getTypeCategory());
        statement.bindString(13, entity.getTagsJson());
        if (entity.getEmbedding() == null) {
          statement.bindNull(14);
        } else {
          statement.bindBlob(14, entity.getEmbedding());
        }
        if (entity.getEmbeddingModel() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getEmbeddingModel());
        }
        statement.bindLong(16, entity.isDeleted());
        if (entity.getDeletedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getDeletedAt());
        }
        statement.bindLong(18, entity.getAiProcessed());
        statement.bindLong(19, entity.getEmbeddingPending());
        statement.bindLong(20, entity.getId());
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE memories SET is_deleted = 1, deleted_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAIStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE memories SET ai_processed = ?, embedding_pending = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateEmbedding = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE memories SET embedding = ?, embedding_model = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Memory memory, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMemory.insertAndReturnId(memory);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Memory memory, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMemory.handle(memory);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Memory memory, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMemory.handle(memory);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final long id, final long deletedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deletedAt);
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
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAIStatus(final long id, final int processed, final int pending,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAIStatus.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, processed);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, pending);
        _argIndex = 3;
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
          __preparedStmtOfUpdateAIStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEmbedding(final long id, final byte[] embedding, final String model,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateEmbedding.acquire();
        int _argIndex = 1;
        _stmt.bindBlob(_argIndex, embedding);
        _argIndex = 2;
        _stmt.bindString(_argIndex, model);
        _argIndex = 3;
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
          __preparedStmtOfUpdateEmbedding.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Memory>> getAllMemories() {
    final String _sql = "SELECT * FROM memories WHERE is_deleted = 0 ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<Memory>>() {
      @Override
      @NonNull
      public List<Memory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfMediaPath = CursorUtil.getColumnIndexOrThrow(_cursor, "media_path");
          final int _cursorIndexOfMediaDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "media_description");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfLocationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lat");
          final int _cursorIndexOfLocationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lng");
          final int _cursorIndexOfLocationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "location_address");
          final int _cursorIndexOfSceneCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "scene_category");
          final int _cursorIndexOfTypeCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "type_category");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
          final int _cursorIndexOfAiProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_processed");
          final int _cursorIndexOfEmbeddingPending = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_pending");
          final List<Memory> _result = new ArrayList<Memory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Memory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpMediaPath;
            if (_cursor.isNull(_cursorIndexOfMediaPath)) {
              _tmpMediaPath = null;
            } else {
              _tmpMediaPath = _cursor.getString(_cursorIndexOfMediaPath);
            }
            final String _tmpMediaDescription;
            if (_cursor.isNull(_cursorIndexOfMediaDescription)) {
              _tmpMediaDescription = null;
            } else {
              _tmpMediaDescription = _cursor.getString(_cursorIndexOfMediaDescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Double _tmpLocationLat;
            if (_cursor.isNull(_cursorIndexOfLocationLat)) {
              _tmpLocationLat = null;
            } else {
              _tmpLocationLat = _cursor.getDouble(_cursorIndexOfLocationLat);
            }
            final Double _tmpLocationLng;
            if (_cursor.isNull(_cursorIndexOfLocationLng)) {
              _tmpLocationLng = null;
            } else {
              _tmpLocationLng = _cursor.getDouble(_cursorIndexOfLocationLng);
            }
            final String _tmpLocationAddress;
            if (_cursor.isNull(_cursorIndexOfLocationAddress)) {
              _tmpLocationAddress = null;
            } else {
              _tmpLocationAddress = _cursor.getString(_cursorIndexOfLocationAddress);
            }
            final String _tmpSceneCategory;
            _tmpSceneCategory = _cursor.getString(_cursorIndexOfSceneCategory);
            final String _tmpTypeCategory;
            _tmpTypeCategory = _cursor.getString(_cursorIndexOfTypeCategory);
            final String _tmpTagsJson;
            _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
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
            final int _tmpIsDeleted;
            _tmpIsDeleted = _cursor.getInt(_cursorIndexOfIsDeleted);
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final int _tmpAiProcessed;
            _tmpAiProcessed = _cursor.getInt(_cursorIndexOfAiProcessed);
            final int _tmpEmbeddingPending;
            _tmpEmbeddingPending = _cursor.getInt(_cursorIndexOfEmbeddingPending);
            _item = new Memory(_tmpId,_tmpContent,_tmpMediaType,_tmpMediaPath,_tmpMediaDescription,_tmpCreatedAt,_tmpUpdatedAt,_tmpLocationLat,_tmpLocationLng,_tmpLocationAddress,_tmpSceneCategory,_tmpTypeCategory,_tmpTagsJson,_tmpEmbedding,_tmpEmbeddingModel,_tmpIsDeleted,_tmpDeletedAt,_tmpAiProcessed,_tmpEmbeddingPending);
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
  public Object getMemoriesPaged(final int limit, final int offset,
      final Continuation<? super List<Memory>> $completion) {
    final String _sql = "SELECT * FROM memories WHERE is_deleted = 0 ORDER BY created_at DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, offset);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Memory>>() {
      @Override
      @NonNull
      public List<Memory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfMediaPath = CursorUtil.getColumnIndexOrThrow(_cursor, "media_path");
          final int _cursorIndexOfMediaDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "media_description");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfLocationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lat");
          final int _cursorIndexOfLocationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lng");
          final int _cursorIndexOfLocationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "location_address");
          final int _cursorIndexOfSceneCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "scene_category");
          final int _cursorIndexOfTypeCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "type_category");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
          final int _cursorIndexOfAiProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_processed");
          final int _cursorIndexOfEmbeddingPending = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_pending");
          final List<Memory> _result = new ArrayList<Memory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Memory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpMediaPath;
            if (_cursor.isNull(_cursorIndexOfMediaPath)) {
              _tmpMediaPath = null;
            } else {
              _tmpMediaPath = _cursor.getString(_cursorIndexOfMediaPath);
            }
            final String _tmpMediaDescription;
            if (_cursor.isNull(_cursorIndexOfMediaDescription)) {
              _tmpMediaDescription = null;
            } else {
              _tmpMediaDescription = _cursor.getString(_cursorIndexOfMediaDescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Double _tmpLocationLat;
            if (_cursor.isNull(_cursorIndexOfLocationLat)) {
              _tmpLocationLat = null;
            } else {
              _tmpLocationLat = _cursor.getDouble(_cursorIndexOfLocationLat);
            }
            final Double _tmpLocationLng;
            if (_cursor.isNull(_cursorIndexOfLocationLng)) {
              _tmpLocationLng = null;
            } else {
              _tmpLocationLng = _cursor.getDouble(_cursorIndexOfLocationLng);
            }
            final String _tmpLocationAddress;
            if (_cursor.isNull(_cursorIndexOfLocationAddress)) {
              _tmpLocationAddress = null;
            } else {
              _tmpLocationAddress = _cursor.getString(_cursorIndexOfLocationAddress);
            }
            final String _tmpSceneCategory;
            _tmpSceneCategory = _cursor.getString(_cursorIndexOfSceneCategory);
            final String _tmpTypeCategory;
            _tmpTypeCategory = _cursor.getString(_cursorIndexOfTypeCategory);
            final String _tmpTagsJson;
            _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
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
            final int _tmpIsDeleted;
            _tmpIsDeleted = _cursor.getInt(_cursorIndexOfIsDeleted);
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final int _tmpAiProcessed;
            _tmpAiProcessed = _cursor.getInt(_cursorIndexOfAiProcessed);
            final int _tmpEmbeddingPending;
            _tmpEmbeddingPending = _cursor.getInt(_cursorIndexOfEmbeddingPending);
            _item = new Memory(_tmpId,_tmpContent,_tmpMediaType,_tmpMediaPath,_tmpMediaDescription,_tmpCreatedAt,_tmpUpdatedAt,_tmpLocationLat,_tmpLocationLng,_tmpLocationAddress,_tmpSceneCategory,_tmpTypeCategory,_tmpTagsJson,_tmpEmbedding,_tmpEmbeddingModel,_tmpIsDeleted,_tmpDeletedAt,_tmpAiProcessed,_tmpEmbeddingPending);
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
  public Object getMemoryById(final long id, final Continuation<? super Memory> $completion) {
    final String _sql = "SELECT * FROM memories WHERE id = ? AND is_deleted = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Memory>() {
      @Override
      @Nullable
      public Memory call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfMediaPath = CursorUtil.getColumnIndexOrThrow(_cursor, "media_path");
          final int _cursorIndexOfMediaDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "media_description");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfLocationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lat");
          final int _cursorIndexOfLocationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lng");
          final int _cursorIndexOfLocationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "location_address");
          final int _cursorIndexOfSceneCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "scene_category");
          final int _cursorIndexOfTypeCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "type_category");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
          final int _cursorIndexOfAiProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_processed");
          final int _cursorIndexOfEmbeddingPending = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_pending");
          final Memory _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpMediaPath;
            if (_cursor.isNull(_cursorIndexOfMediaPath)) {
              _tmpMediaPath = null;
            } else {
              _tmpMediaPath = _cursor.getString(_cursorIndexOfMediaPath);
            }
            final String _tmpMediaDescription;
            if (_cursor.isNull(_cursorIndexOfMediaDescription)) {
              _tmpMediaDescription = null;
            } else {
              _tmpMediaDescription = _cursor.getString(_cursorIndexOfMediaDescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Double _tmpLocationLat;
            if (_cursor.isNull(_cursorIndexOfLocationLat)) {
              _tmpLocationLat = null;
            } else {
              _tmpLocationLat = _cursor.getDouble(_cursorIndexOfLocationLat);
            }
            final Double _tmpLocationLng;
            if (_cursor.isNull(_cursorIndexOfLocationLng)) {
              _tmpLocationLng = null;
            } else {
              _tmpLocationLng = _cursor.getDouble(_cursorIndexOfLocationLng);
            }
            final String _tmpLocationAddress;
            if (_cursor.isNull(_cursorIndexOfLocationAddress)) {
              _tmpLocationAddress = null;
            } else {
              _tmpLocationAddress = _cursor.getString(_cursorIndexOfLocationAddress);
            }
            final String _tmpSceneCategory;
            _tmpSceneCategory = _cursor.getString(_cursorIndexOfSceneCategory);
            final String _tmpTypeCategory;
            _tmpTypeCategory = _cursor.getString(_cursorIndexOfTypeCategory);
            final String _tmpTagsJson;
            _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
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
            final int _tmpIsDeleted;
            _tmpIsDeleted = _cursor.getInt(_cursorIndexOfIsDeleted);
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final int _tmpAiProcessed;
            _tmpAiProcessed = _cursor.getInt(_cursorIndexOfAiProcessed);
            final int _tmpEmbeddingPending;
            _tmpEmbeddingPending = _cursor.getInt(_cursorIndexOfEmbeddingPending);
            _result = new Memory(_tmpId,_tmpContent,_tmpMediaType,_tmpMediaPath,_tmpMediaDescription,_tmpCreatedAt,_tmpUpdatedAt,_tmpLocationLat,_tmpLocationLng,_tmpLocationAddress,_tmpSceneCategory,_tmpTypeCategory,_tmpTagsJson,_tmpEmbedding,_tmpEmbeddingModel,_tmpIsDeleted,_tmpDeletedAt,_tmpAiProcessed,_tmpEmbeddingPending);
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
  public Flow<List<Memory>> getMemoriesByCategory(final String category) {
    final String _sql = "SELECT * FROM memories WHERE is_deleted = 0 AND scene_category = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<Memory>>() {
      @Override
      @NonNull
      public List<Memory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfMediaPath = CursorUtil.getColumnIndexOrThrow(_cursor, "media_path");
          final int _cursorIndexOfMediaDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "media_description");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfLocationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lat");
          final int _cursorIndexOfLocationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lng");
          final int _cursorIndexOfLocationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "location_address");
          final int _cursorIndexOfSceneCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "scene_category");
          final int _cursorIndexOfTypeCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "type_category");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
          final int _cursorIndexOfAiProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_processed");
          final int _cursorIndexOfEmbeddingPending = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_pending");
          final List<Memory> _result = new ArrayList<Memory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Memory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpMediaPath;
            if (_cursor.isNull(_cursorIndexOfMediaPath)) {
              _tmpMediaPath = null;
            } else {
              _tmpMediaPath = _cursor.getString(_cursorIndexOfMediaPath);
            }
            final String _tmpMediaDescription;
            if (_cursor.isNull(_cursorIndexOfMediaDescription)) {
              _tmpMediaDescription = null;
            } else {
              _tmpMediaDescription = _cursor.getString(_cursorIndexOfMediaDescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Double _tmpLocationLat;
            if (_cursor.isNull(_cursorIndexOfLocationLat)) {
              _tmpLocationLat = null;
            } else {
              _tmpLocationLat = _cursor.getDouble(_cursorIndexOfLocationLat);
            }
            final Double _tmpLocationLng;
            if (_cursor.isNull(_cursorIndexOfLocationLng)) {
              _tmpLocationLng = null;
            } else {
              _tmpLocationLng = _cursor.getDouble(_cursorIndexOfLocationLng);
            }
            final String _tmpLocationAddress;
            if (_cursor.isNull(_cursorIndexOfLocationAddress)) {
              _tmpLocationAddress = null;
            } else {
              _tmpLocationAddress = _cursor.getString(_cursorIndexOfLocationAddress);
            }
            final String _tmpSceneCategory;
            _tmpSceneCategory = _cursor.getString(_cursorIndexOfSceneCategory);
            final String _tmpTypeCategory;
            _tmpTypeCategory = _cursor.getString(_cursorIndexOfTypeCategory);
            final String _tmpTagsJson;
            _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
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
            final int _tmpIsDeleted;
            _tmpIsDeleted = _cursor.getInt(_cursorIndexOfIsDeleted);
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final int _tmpAiProcessed;
            _tmpAiProcessed = _cursor.getInt(_cursorIndexOfAiProcessed);
            final int _tmpEmbeddingPending;
            _tmpEmbeddingPending = _cursor.getInt(_cursorIndexOfEmbeddingPending);
            _item = new Memory(_tmpId,_tmpContent,_tmpMediaType,_tmpMediaPath,_tmpMediaDescription,_tmpCreatedAt,_tmpUpdatedAt,_tmpLocationLat,_tmpLocationLng,_tmpLocationAddress,_tmpSceneCategory,_tmpTypeCategory,_tmpTagsJson,_tmpEmbedding,_tmpEmbeddingModel,_tmpIsDeleted,_tmpDeletedAt,_tmpAiProcessed,_tmpEmbeddingPending);
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
  public Flow<List<Memory>> getMemoriesByTag(final String tagQuery) {
    final String _sql = "SELECT * FROM memories WHERE is_deleted = 0 AND tags LIKE ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tagQuery);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memories"}, new Callable<List<Memory>>() {
      @Override
      @NonNull
      public List<Memory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfMediaPath = CursorUtil.getColumnIndexOrThrow(_cursor, "media_path");
          final int _cursorIndexOfMediaDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "media_description");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfLocationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lat");
          final int _cursorIndexOfLocationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lng");
          final int _cursorIndexOfLocationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "location_address");
          final int _cursorIndexOfSceneCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "scene_category");
          final int _cursorIndexOfTypeCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "type_category");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
          final int _cursorIndexOfAiProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_processed");
          final int _cursorIndexOfEmbeddingPending = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_pending");
          final List<Memory> _result = new ArrayList<Memory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Memory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpMediaPath;
            if (_cursor.isNull(_cursorIndexOfMediaPath)) {
              _tmpMediaPath = null;
            } else {
              _tmpMediaPath = _cursor.getString(_cursorIndexOfMediaPath);
            }
            final String _tmpMediaDescription;
            if (_cursor.isNull(_cursorIndexOfMediaDescription)) {
              _tmpMediaDescription = null;
            } else {
              _tmpMediaDescription = _cursor.getString(_cursorIndexOfMediaDescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Double _tmpLocationLat;
            if (_cursor.isNull(_cursorIndexOfLocationLat)) {
              _tmpLocationLat = null;
            } else {
              _tmpLocationLat = _cursor.getDouble(_cursorIndexOfLocationLat);
            }
            final Double _tmpLocationLng;
            if (_cursor.isNull(_cursorIndexOfLocationLng)) {
              _tmpLocationLng = null;
            } else {
              _tmpLocationLng = _cursor.getDouble(_cursorIndexOfLocationLng);
            }
            final String _tmpLocationAddress;
            if (_cursor.isNull(_cursorIndexOfLocationAddress)) {
              _tmpLocationAddress = null;
            } else {
              _tmpLocationAddress = _cursor.getString(_cursorIndexOfLocationAddress);
            }
            final String _tmpSceneCategory;
            _tmpSceneCategory = _cursor.getString(_cursorIndexOfSceneCategory);
            final String _tmpTypeCategory;
            _tmpTypeCategory = _cursor.getString(_cursorIndexOfTypeCategory);
            final String _tmpTagsJson;
            _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
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
            final int _tmpIsDeleted;
            _tmpIsDeleted = _cursor.getInt(_cursorIndexOfIsDeleted);
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final int _tmpAiProcessed;
            _tmpAiProcessed = _cursor.getInt(_cursorIndexOfAiProcessed);
            final int _tmpEmbeddingPending;
            _tmpEmbeddingPending = _cursor.getInt(_cursorIndexOfEmbeddingPending);
            _item = new Memory(_tmpId,_tmpContent,_tmpMediaType,_tmpMediaPath,_tmpMediaDescription,_tmpCreatedAt,_tmpUpdatedAt,_tmpLocationLat,_tmpLocationLng,_tmpLocationAddress,_tmpSceneCategory,_tmpTypeCategory,_tmpTagsJson,_tmpEmbedding,_tmpEmbeddingModel,_tmpIsDeleted,_tmpDeletedAt,_tmpAiProcessed,_tmpEmbeddingPending);
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
  public Object searchByKeyword(final String query,
      final Continuation<? super List<Memory>> $completion) {
    final String _sql = "SELECT * FROM memories WHERE is_deleted = 0 AND content LIKE ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Memory>>() {
      @Override
      @NonNull
      public List<Memory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfMediaPath = CursorUtil.getColumnIndexOrThrow(_cursor, "media_path");
          final int _cursorIndexOfMediaDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "media_description");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfLocationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lat");
          final int _cursorIndexOfLocationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lng");
          final int _cursorIndexOfLocationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "location_address");
          final int _cursorIndexOfSceneCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "scene_category");
          final int _cursorIndexOfTypeCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "type_category");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
          final int _cursorIndexOfAiProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_processed");
          final int _cursorIndexOfEmbeddingPending = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_pending");
          final List<Memory> _result = new ArrayList<Memory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Memory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpMediaPath;
            if (_cursor.isNull(_cursorIndexOfMediaPath)) {
              _tmpMediaPath = null;
            } else {
              _tmpMediaPath = _cursor.getString(_cursorIndexOfMediaPath);
            }
            final String _tmpMediaDescription;
            if (_cursor.isNull(_cursorIndexOfMediaDescription)) {
              _tmpMediaDescription = null;
            } else {
              _tmpMediaDescription = _cursor.getString(_cursorIndexOfMediaDescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Double _tmpLocationLat;
            if (_cursor.isNull(_cursorIndexOfLocationLat)) {
              _tmpLocationLat = null;
            } else {
              _tmpLocationLat = _cursor.getDouble(_cursorIndexOfLocationLat);
            }
            final Double _tmpLocationLng;
            if (_cursor.isNull(_cursorIndexOfLocationLng)) {
              _tmpLocationLng = null;
            } else {
              _tmpLocationLng = _cursor.getDouble(_cursorIndexOfLocationLng);
            }
            final String _tmpLocationAddress;
            if (_cursor.isNull(_cursorIndexOfLocationAddress)) {
              _tmpLocationAddress = null;
            } else {
              _tmpLocationAddress = _cursor.getString(_cursorIndexOfLocationAddress);
            }
            final String _tmpSceneCategory;
            _tmpSceneCategory = _cursor.getString(_cursorIndexOfSceneCategory);
            final String _tmpTypeCategory;
            _tmpTypeCategory = _cursor.getString(_cursorIndexOfTypeCategory);
            final String _tmpTagsJson;
            _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
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
            final int _tmpIsDeleted;
            _tmpIsDeleted = _cursor.getInt(_cursorIndexOfIsDeleted);
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final int _tmpAiProcessed;
            _tmpAiProcessed = _cursor.getInt(_cursorIndexOfAiProcessed);
            final int _tmpEmbeddingPending;
            _tmpEmbeddingPending = _cursor.getInt(_cursorIndexOfEmbeddingPending);
            _item = new Memory(_tmpId,_tmpContent,_tmpMediaType,_tmpMediaPath,_tmpMediaDescription,_tmpCreatedAt,_tmpUpdatedAt,_tmpLocationLat,_tmpLocationLng,_tmpLocationAddress,_tmpSceneCategory,_tmpTypeCategory,_tmpTagsJson,_tmpEmbedding,_tmpEmbeddingModel,_tmpIsDeleted,_tmpDeletedAt,_tmpAiProcessed,_tmpEmbeddingPending);
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
  public Object getMemoriesWithEmbedding(final Continuation<? super List<Memory>> $completion) {
    final String _sql = "SELECT * FROM memories WHERE is_deleted = 0 AND embedding IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Memory>>() {
      @Override
      @NonNull
      public List<Memory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "media_type");
          final int _cursorIndexOfMediaPath = CursorUtil.getColumnIndexOrThrow(_cursor, "media_path");
          final int _cursorIndexOfMediaDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "media_description");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfLocationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lat");
          final int _cursorIndexOfLocationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "location_lng");
          final int _cursorIndexOfLocationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "location_address");
          final int _cursorIndexOfSceneCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "scene_category");
          final int _cursorIndexOfTypeCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "type_category");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
          final int _cursorIndexOfAiProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_processed");
          final int _cursorIndexOfEmbeddingPending = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_pending");
          final List<Memory> _result = new ArrayList<Memory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Memory _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpMediaPath;
            if (_cursor.isNull(_cursorIndexOfMediaPath)) {
              _tmpMediaPath = null;
            } else {
              _tmpMediaPath = _cursor.getString(_cursorIndexOfMediaPath);
            }
            final String _tmpMediaDescription;
            if (_cursor.isNull(_cursorIndexOfMediaDescription)) {
              _tmpMediaDescription = null;
            } else {
              _tmpMediaDescription = _cursor.getString(_cursorIndexOfMediaDescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Double _tmpLocationLat;
            if (_cursor.isNull(_cursorIndexOfLocationLat)) {
              _tmpLocationLat = null;
            } else {
              _tmpLocationLat = _cursor.getDouble(_cursorIndexOfLocationLat);
            }
            final Double _tmpLocationLng;
            if (_cursor.isNull(_cursorIndexOfLocationLng)) {
              _tmpLocationLng = null;
            } else {
              _tmpLocationLng = _cursor.getDouble(_cursorIndexOfLocationLng);
            }
            final String _tmpLocationAddress;
            if (_cursor.isNull(_cursorIndexOfLocationAddress)) {
              _tmpLocationAddress = null;
            } else {
              _tmpLocationAddress = _cursor.getString(_cursorIndexOfLocationAddress);
            }
            final String _tmpSceneCategory;
            _tmpSceneCategory = _cursor.getString(_cursorIndexOfSceneCategory);
            final String _tmpTypeCategory;
            _tmpTypeCategory = _cursor.getString(_cursorIndexOfTypeCategory);
            final String _tmpTagsJson;
            _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
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
            final int _tmpIsDeleted;
            _tmpIsDeleted = _cursor.getInt(_cursorIndexOfIsDeleted);
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final int _tmpAiProcessed;
            _tmpAiProcessed = _cursor.getInt(_cursorIndexOfAiProcessed);
            final int _tmpEmbeddingPending;
            _tmpEmbeddingPending = _cursor.getInt(_cursorIndexOfEmbeddingPending);
            _item = new Memory(_tmpId,_tmpContent,_tmpMediaType,_tmpMediaPath,_tmpMediaDescription,_tmpCreatedAt,_tmpUpdatedAt,_tmpLocationLat,_tmpLocationLng,_tmpLocationAddress,_tmpSceneCategory,_tmpTypeCategory,_tmpTagsJson,_tmpEmbedding,_tmpEmbeddingModel,_tmpIsDeleted,_tmpDeletedAt,_tmpAiProcessed,_tmpEmbeddingPending);
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
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM memories WHERE is_deleted = 0";
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
  public Object getAllCategories(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT scene_category FROM memories WHERE is_deleted = 0";
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

  @Override
  public Object getAllTags(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT value FROM memories, json_each(tags) WHERE is_deleted = 0";
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
            final String _tmp;
            _tmp = _cursor.getString(0);
            _item = _tmp;
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
