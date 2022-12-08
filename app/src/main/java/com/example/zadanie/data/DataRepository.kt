package com.example.zadanie.data

import android.util.Log
import com.example.zadanie.data.api.*
import com.example.zadanie.data.db.dao.BarsDao
import com.example.zadanie.data.db.dao.FriendsDao
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.data.db.model.FriendItem
import com.example.zadanie.ui.viewmodels.SortBy
import com.example.zadanie.ui.viewmodels.data.MyLocation
import com.example.zadanie.ui.viewmodels.data.NearbyBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DataRepository private constructor(
    private val service: RestApi,
    private val barsCache: BarsDao,
    private val friendsCache: FriendsDao,
){

    suspend fun apiUserCreate(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserResponse?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val resp = service.userCreate(UserCreateRequest(name = name, password = password))
                if (resp.isSuccessful) {
                    resp.body()?.let { user ->
                        if (user.uid == "-1") {
                            onStatus(null)
                            onError("Name already exists. Choose another.")
                        } else {
                            onStatus(user)
                            friendsCache.deleteFriends()
                        }
                    }
                } else {
                    onError("Failed to sign up, try again later.")
                    onStatus(null)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Sign up failed, check internet connection.")
                onStatus(null)
            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Sign up failed, error.")
                onStatus(null)
            }
        }
    }

    suspend fun apiUserLogin(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserResponse?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val resp = service.userLogin(UserLoginRequest(name = name, password = password))
                if (resp.isSuccessful) {
                    resp.body()?.let { user ->
                        if (user.uid == "-1") {
                            onStatus(null)
                            onError("Wrong name or password.")
                        } else {
                            onStatus(user)
                            friendsCache.deleteFriends()
                        }
                    }
                } else {
                    onError("Failed to login, try again later.")
                    onStatus(null)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Login failed, check internet connection.")
                onStatus(null)
            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Login in failed, error.")
                onStatus(null)
            }
        }
    }

    suspend fun apiBarCheckin(
        bar: NearbyBar,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val resp = service.barMessage(BarMessageRequest(bar.id,bar.name,bar.type,bar.lat,bar.lon))
                if (resp.isSuccessful) {
                    resp.body()?.let { user ->
                        onSuccess(true)
                    }
                } else {
                    onError("Failed to login, try again later.")
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Login failed, check internet connection.")
            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Login in failed, error.")
            }
        }
    }

    suspend fun apiBarList(
        onError: (error: String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val resp = service.barList()
                if (resp.isSuccessful) {
                    resp.body()?.let { bars ->

                        val b = bars.map {
                            BarItem(
                                it.bar_id,
                                it.bar_name,
                                it.bar_type,
                                it.lat,
                                it.lon,
                                it.users
                            )
                        }
                        barsCache.deleteBars()
                        barsCache.insertBars(b)
                    } ?: onError("Failed to load bars.")
                } else {
                    onError("Failed to read bars.")
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Failed to load bars, check internet connection.")
            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Failed to load bars, error.")
            }
        }
    }

    suspend fun apiNearbyBars(
        lat: Double, lon: Double,
        onError: (error: String) -> Unit
    ) : List<NearbyBar> {
        var nearby = listOf<NearbyBar>()
        withContext(Dispatchers.IO) {
            try {
                val q =
                    "[out:json];node(around:250,$lat,$lon);(node(around:250)[\"amenity\"~\"^pub$|^bar$|^restaurant$|^cafe$|^fast_food$|^stripclub$|^nightclub$\"];);out body;>;out skel;"
                val resp = service.barNearby(q)
                if (resp.isSuccessful) {
                    resp.body()?.let { bars ->
                        nearby = bars.elements.map {
                            NearbyBar(it.id,it.tags.getOrDefault("name",""), it.tags.getOrDefault("amenity",""),it.lat,it.lon,it.tags).apply {
                                distance = distanceTo(MyLocation(lat,lon))
                            }
                        }
                        nearby = nearby.filter { it.name.isNotBlank() }.sortedBy { it.distance }
                    } ?: onError("Failed to load bars.")
                } else {
                    onError("Failed to read bars.")
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Failed to load bars, check internet connection.")
            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Failed to load bars, error.")
            }
        }
        return nearby
    }

    suspend fun apiBarDetail(
        id: String,
        onError: (error: String) -> Unit
    ) : NearbyBar? {
        var nearby:NearbyBar? = null
        withContext(Dispatchers.IO) {
            try {
                val q = "[out:json];node($id);out body;>;out skel;"
                val resp = service.barDetail(q)
                if (resp.isSuccessful) {
                    resp.body()?.let { bars ->
                        if (bars.elements.isNotEmpty()) {
                            val b = bars.elements.get(0)
                            nearby = NearbyBar(
                                b.id,
                                b.tags.getOrDefault("name", ""),
                                b.tags.getOrDefault("amenity", ""),
                                b.lat,
                                b.lon,
                                b.tags
                            )
                        }
                    } ?: onError("Failed to load bars.")
                } else {
                    onError("Failed to read bars.")
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Failed to load bars, check internet connection.")
            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Failed to load bars, error.")
            }
        }
        return nearby
    }

    suspend fun dbUsersByBarId(id: String) : Int {
        var users: Int
        withContext(Dispatchers.IO) {
            users = barsCache.getUsersByBarId(id)
            Log.i("users", users.toString())
        }
        return users
    }

    suspend fun dbBars(isAsc: Boolean, sortBy: SortBy?) : List<BarItem>? {
        var bars: List<BarItem>?
        withContext(Dispatchers.IO) {
            bars = when(sortBy){
                SortBy.NAME -> barsCache.getBarsName(isAsc)
                SortBy.USERS -> barsCache.getBarsUsers(isAsc)
                else -> barsCache.getBars(isAsc)
            }
        }
        return bars
    }

    suspend fun dbFriends() : List<FriendItem>? {
        var friends: List<FriendItem>?
        withContext(Dispatchers.IO) {
            friends = friendsCache.allFriends()
        }
        return friends
    }

    suspend fun apiFriendsList(onError: (error: String) -> Unit){
        withContext(Dispatchers.IO){
            try {
                val resp = service.userFriendsList()
                if (resp.isSuccessful) {
                    resp.body()?.let { friends ->
                        val f = friends.map {
                            FriendItem(
                                it.user_id,
                                it.user_name,
                                it.bar_id,
                                it.bar_name,
                                it.time,
                                it.bar_lat,
                                it.bar_lon
                            )
                        }
                        friendsCache.deleteFriends()
                        friendsCache.insertFriends(f)
                    } ?: onError("Failed to load friends.")
                } else {
                    onError("Failed to read friends.")
                }
            }catch (ex: IOException){
                onError("Failed to load friends, check internet connection.")
            }catch (ex: IOException){
                onError("Failed to load friends, error.")
            }
        }
    }
    suspend fun apiAddFriend(
        contact: String,
        onError: (error: String) -> Unit,
        onSuccess: (success: String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val resp = service.friendAdd(ContactAddRequest(contact = contact))
                if (resp.isSuccessful) {
                   onSuccess("Friend was added.")
                } else {
                    onError("Failed to add friend, try again later.")
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Add friend failed, check internet connection.")
            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Add friend failed, error.")
            }
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: DataRepository? = null

        fun getInstance(service: RestApi, barsCache: BarsDao, friendsCache: FriendsDao): DataRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: DataRepository(service, barsCache, friendsCache).also { INSTANCE = it }
            }

    }
}