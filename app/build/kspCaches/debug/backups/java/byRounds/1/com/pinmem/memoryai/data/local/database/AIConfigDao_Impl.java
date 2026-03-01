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
import com.pinmem.memoryai.data.model.AIConfig;
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
public final class AIConfigDao_Impl implements AIConfigDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AIConfig> __insertionAdapterOfAIConfig;

  private final EntityDeletionOrUpdateAdapter<AIConfig> __deletionAdapterOfAIConfig;

  private final EntityDeletionOrUpdateAdapter<AIConfig> __updateAdapterOfAIConfig;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAll;

  private final SharedSQLiteStatement __preparedStmtOfActivateConfig;

  public AIConfigDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAIConfig = new EntityInsertionAdapter<AIConfig>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ai_config` (`id`,`provider`,`api_key`,`base_url`,`embedding_model`,`llm_model`,`is_active`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AIConfig entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getProvider());
        statement.bindString(3, entity.getApiKey());
        if (entity.getBaseUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getBaseUrl());
        }
        statement.bindString(5, entity.getEmbeddingModel());
        statement.bindString(6, entity.getLlmModel());
        statement.bindLong(7, entity.isActive());
      }
    };
    this.__deletionAdapterOfAIConfig = new EntityDeletionOrUpdateAdapter<AIConfig>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ai_config` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AIConfig entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAIConfig = new EntityDeletionOrUpdateAdapter<AIConfig>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `ai_config` SET `id` = ?,`provider` = ?,`api_key` = ?,`base_url` = ?,`embedding_model` = ?,`llm_model` = ?,`is_active` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AIConfig entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getProvider());
        statement.bindString(3, entity.getApiKey());
        if (entity.getBaseUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getBaseUrl());
        }
        statement.bindString(5, entity.getEmbeddingModel());
        statement.bindString(6, entity.getLlmModel());
        statement.bindLong(7, entity.isActive());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeactivateAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE ai_config SET is_active = 0";
        return _query;
      }
    };
    this.__preparedStmtOfActivateConfig = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE ai_config SET is_active = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AIConfig config, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAIConfig.insertAndReturnId(config);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final AIConfig config, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAIConfig.handle(config);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AIConfig config, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAIConfig.handle(config);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAll.acquire();
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
          __preparedStmtOfDeactivateAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object activateConfig(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActivateConfig.acquire();
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
          __preparedStmtOfActivateConfig.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<AIConfig> getActiveConfig() {
    final String _sql = "SELECT * FROM ai_config WHERE is_active = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ai_config"}, new Callable<AIConfig>() {
      @Override
      @Nullable
      public AIConfig call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfApiKey = CursorUtil.getColumnIndexOrThrow(_cursor, "api_key");
          final int _cursorIndexOfBaseUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "base_url");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfLlmModel = CursorUtil.getColumnIndexOrThrow(_cursor, "llm_model");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final AIConfig _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpApiKey;
            _tmpApiKey = _cursor.getString(_cursorIndexOfApiKey);
            final String _tmpBaseUrl;
            if (_cursor.isNull(_cursorIndexOfBaseUrl)) {
              _tmpBaseUrl = null;
            } else {
              _tmpBaseUrl = _cursor.getString(_cursorIndexOfBaseUrl);
            }
            final String _tmpEmbeddingModel;
            _tmpEmbeddingModel = _cursor.getString(_cursorIndexOfEmbeddingModel);
            final String _tmpLlmModel;
            _tmpLlmModel = _cursor.getString(_cursorIndexOfLlmModel);
            final int _tmpIsActive;
            _tmpIsActive = _cursor.getInt(_cursorIndexOfIsActive);
            _result = new AIConfig(_tmpId,_tmpProvider,_tmpApiKey,_tmpBaseUrl,_tmpEmbeddingModel,_tmpLlmModel,_tmpIsActive);
          } else {
            _result = null;
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
  public Object getActiveConfigOnce(final Continuation<? super AIConfig> $completion) {
    final String _sql = "SELECT * FROM ai_config WHERE is_active = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AIConfig>() {
      @Override
      @Nullable
      public AIConfig call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfApiKey = CursorUtil.getColumnIndexOrThrow(_cursor, "api_key");
          final int _cursorIndexOfBaseUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "base_url");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfLlmModel = CursorUtil.getColumnIndexOrThrow(_cursor, "llm_model");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final AIConfig _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpApiKey;
            _tmpApiKey = _cursor.getString(_cursorIndexOfApiKey);
            final String _tmpBaseUrl;
            if (_cursor.isNull(_cursorIndexOfBaseUrl)) {
              _tmpBaseUrl = null;
            } else {
              _tmpBaseUrl = _cursor.getString(_cursorIndexOfBaseUrl);
            }
            final String _tmpEmbeddingModel;
            _tmpEmbeddingModel = _cursor.getString(_cursorIndexOfEmbeddingModel);
            final String _tmpLlmModel;
            _tmpLlmModel = _cursor.getString(_cursorIndexOfLlmModel);
            final int _tmpIsActive;
            _tmpIsActive = _cursor.getInt(_cursorIndexOfIsActive);
            _result = new AIConfig(_tmpId,_tmpProvider,_tmpApiKey,_tmpBaseUrl,_tmpEmbeddingModel,_tmpLlmModel,_tmpIsActive);
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
  public Object getAllConfigs(final Continuation<? super List<AIConfig>> $completion) {
    final String _sql = "SELECT * FROM ai_config";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AIConfig>>() {
      @Override
      @NonNull
      public List<AIConfig> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfApiKey = CursorUtil.getColumnIndexOrThrow(_cursor, "api_key");
          final int _cursorIndexOfBaseUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "base_url");
          final int _cursorIndexOfEmbeddingModel = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding_model");
          final int _cursorIndexOfLlmModel = CursorUtil.getColumnIndexOrThrow(_cursor, "llm_model");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final List<AIConfig> _result = new ArrayList<AIConfig>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AIConfig _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpApiKey;
            _tmpApiKey = _cursor.getString(_cursorIndexOfApiKey);
            final String _tmpBaseUrl;
            if (_cursor.isNull(_cursorIndexOfBaseUrl)) {
              _tmpBaseUrl = null;
            } else {
              _tmpBaseUrl = _cursor.getString(_cursorIndexOfBaseUrl);
            }
            final String _tmpEmbeddingModel;
            _tmpEmbeddingModel = _cursor.getString(_cursorIndexOfEmbeddingModel);
            final String _tmpLlmModel;
            _tmpLlmModel = _cursor.getString(_cursorIndexOfLlmModel);
            final int _tmpIsActive;
            _tmpIsActive = _cursor.getInt(_cursorIndexOfIsActive);
            _item = new AIConfig(_tmpId,_tmpProvider,_tmpApiKey,_tmpBaseUrl,_tmpEmbeddingModel,_tmpLlmModel,_tmpIsActive);
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
