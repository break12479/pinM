package com.pinmem.memoryai.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.*
import com.pinmem.memoryai.data.model.LocationInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

/**
 * 位置服务工具
 */
class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * 获取最后已知位置
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    /**
     * 获取当前位置（带超时）
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(timeoutMillis: Long = 5000): Location? {
        return try {
            // 先尝试获取最后已知位置
            val lastLocation = getLastLocation()
            if (lastLocation != null) {
                val age = System.currentTimeMillis() - lastLocation.time
                if (age < timeoutMillis) {
                    return lastLocation
                }
            }

            // 请求更新
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                timeoutMillis
            ).build()

            var location: Location? = null

            // 使用回调获取位置
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        location = result.lastLocation
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                },
                null
            )

            // 等待位置更新
            kotlinx.coroutines.delay(timeoutMillis)
            location

        } catch (e: SecurityException) {
            AppLogger.w("Location permission denied", e)
            null
        } catch (e: Exception) {
            AppLogger.w("Get location failed", e)
            null
        }
    }

    /**
     * 根据坐标获取地址
     */
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0)
        } catch (e: IOException) {
            AppLogger.w("Geocoder failed", e)
            null
        }
    }

    /**
     * 获取位置信息 Flow
     */
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, callback, null)
        } catch (e: SecurityException) {
            close(e)
        }

        invokeOnClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
}

/**
 * 从 Android Location 转换为 LocationInfo
 */
fun Location.toLocationInfo(address: String? = null): LocationInfo {
    return LocationInfo(
        latitude = latitude,
        longitude = longitude,
        address = address
    )
}
