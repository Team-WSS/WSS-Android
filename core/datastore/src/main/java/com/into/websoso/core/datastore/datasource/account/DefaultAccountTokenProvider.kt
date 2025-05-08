package com.into.websoso.core.datastore.datasource.account

import com.into.websoso.data.account.AccountTokenProvider
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import javax.inject.Inject

internal class DefaultAccountTokenProvider
    @Inject
    constructor(
        private val accountDataStore: AccountLocalDataSource,
    ) : AccountTokenProvider {
        override suspend fun invoke() {
            accountDataStore.accessToken()
        }
    }
