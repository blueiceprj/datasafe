package de.adorsys.datasafe.business.impl.privatestore.actions;

import dagger.Binds;
import dagger.Module;
import de.adorsys.datasafe.privatestore.api.PrivateSpaceService;
import de.adorsys.datasafe.privatestore.impl.PrivateSpaceServiceImpl;
import de.adorsys.datasafe.privatestore.impl.actions.*;
import de.adorsys.datasafe.privatestore.api.actions.*;

/**
 * This module is responsible for providing default actions on PRIVATE folder.
 */
@Module
public abstract class DefaultPrivateActionsModule {

    @Binds
    abstract EncryptedResourceResolver encryptedResourceResolver(EncryptedResourceResolverImpl impl);

    @Binds
    abstract ListPrivate listPrivate(ListPrivateImpl impl);

    @Binds
    abstract ReadFromPrivate readFromPrivate(ReadFromPrivateImpl impl);

    @Binds
    abstract WriteToPrivate writeToPrivate(WriteToPrivateImpl impl);

    @Binds
    abstract PrivateSpaceService privateSpaceService(PrivateSpaceServiceImpl impl);

    @Binds
    abstract RemoveFromPrivate removeFromPrivate(RemoveFromPrivateImpl impl);
}