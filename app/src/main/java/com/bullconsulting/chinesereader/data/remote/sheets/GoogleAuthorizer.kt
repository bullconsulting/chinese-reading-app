package com.bullconsulting.chinesereader.data.remote.sheets

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps Google's on-device authorization. Requests the read-only Sheets scope and
 * yields an OAuth access token (prompting the user for consent the first time).
 */
@Singleton
class GoogleAuthorizer @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val client = Identity.getAuthorizationClient(context)

    private val request = AuthorizationRequest.builder()
        .setRequestedScopes(listOf(Scope("https://www.googleapis.com/auth/spreadsheets.readonly")))
        .build()

    /** Returns the authorization result. May require a consent screen (hasResolution). */
    suspend fun authorize(): AuthorizationResult = client.authorize(request).await()

    /** Extracts the access token after the user completes the consent screen. */
    fun tokenFromIntent(intent: Intent): String? =
        client.getAuthorizationResultFromIntent(intent).accessToken
}
