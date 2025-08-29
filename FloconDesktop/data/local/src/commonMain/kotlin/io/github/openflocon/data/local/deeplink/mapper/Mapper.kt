package io.github.openflocon.data.local.deeplink.mapper

import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

fun DeeplinkEntity.toDomainModel(): DeeplinkDomainModel = DeeplinkDomainModel(
    label = this.label,
    link = this.link,
    description = this.description,
)

fun DeeplinkDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
): DeeplinkEntity {
    // Note: The ID will be generated automatically by Room during insertion,
    // so we don't need to specify it here if we're doing a new insertion.
    // If you're updating an existing entity, you may need to pass the ID.
    return DeeplinkEntity(
        link = link,
        deviceId = deviceIdAndPackageName.deviceId,
        label = label,
        packageName = deviceIdAndPackageName.packageName,
        description = description,
    )
}

// For a list
fun toDomainModels(entities: List<DeeplinkEntity>): List<DeeplinkDomainModel> = entities.map { it.toDomainModel() }

fun toEntities(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, deeplinks: List<DeeplinkDomainModel>): List<DeeplinkEntity> =
    deeplinks.map {
        it.toEntity(deviceIdAndPackageName = deviceIdAndPackageName)
    }
