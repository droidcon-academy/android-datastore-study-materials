package com.droidcon.comicsworld.data.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.droidcon.comicsworld.UserComicPreference
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserComicPreference> {
    override val defaultValue: UserComicPreference
        get() = UserComicPreference.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserComicPreference {
        return try {
            UserComicPreference.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read from the proto file because of $ex")
        }
    }

    override suspend fun writeTo(t: UserComicPreference, output: OutputStream) {
        t.writeTo(output)
    }
}