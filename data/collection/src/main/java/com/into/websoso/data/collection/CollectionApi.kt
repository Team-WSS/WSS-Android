package com.into.websoso.data.collection

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CollectionApi {
    @POST("collections")
    suspend fun createCollection(
        @Body request: SaveCollectionRequestDto,
    ): CreateCollectionResponseDto

    @GET("collections/{collectionId}")
    suspend fun getCollection(
        @Path("collectionId") collectionId: Long,
        @Query("sortCriteria") sortCriteria: String,
    ): CollectionDetailResponseDto

    @PUT("collections/{collectionId}")
    suspend fun updateCollection(
        @Path("collectionId") collectionId: Long,
        @Body request: SaveCollectionRequestDto,
    )

    @DELETE("collections/{collectionId}")
    suspend fun deleteCollection(
        @Path("collectionId") collectionId: Long,
    )

    @PUT("collections/{collectionId}/likes")
    suspend fun likeCollection(
        @Path("collectionId") collectionId: Long,
    )

    @DELETE("collections/{collectionId}/likes")
    suspend fun unlikeCollection(
        @Path("collectionId") collectionId: Long,
    )

    @GET("users/me/liked-collections")
    suspend fun getLikedCollections(
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
    ): CollectionPageResponseDto

    @GET("users/{userId}/collections")
    suspend fun getUserCollections(
        @Path("userId") userId: Long,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
    ): CollectionPageResponseDto
}
