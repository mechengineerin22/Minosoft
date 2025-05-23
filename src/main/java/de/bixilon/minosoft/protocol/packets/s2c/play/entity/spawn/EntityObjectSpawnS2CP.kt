/*
 * Minosoft
 * Copyright (C) 2020-2025 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */
package de.bixilon.minosoft.protocol.packets.s2c.play.entity.spawn

import de.bixilon.kotlinglm.vec3.Vec3d
import de.bixilon.minosoft.data.entities.EntityRotation
import de.bixilon.minosoft.data.entities.entities.Entity
import de.bixilon.minosoft.protocol.network.session.play.PlaySession
import de.bixilon.minosoft.protocol.packets.s2c.PlayS2CPacket
import de.bixilon.minosoft.protocol.protocol.ProtocolVersions
import de.bixilon.minosoft.protocol.protocol.buffers.play.PlayInByteBuffer
import de.bixilon.minosoft.util.KUtil.startInit
import de.bixilon.minosoft.util.logging.Log
import de.bixilon.minosoft.util.logging.LogLevels
import de.bixilon.minosoft.util.logging.LogMessageType
import java.util.*

class EntityObjectSpawnS2CP(buffer: PlayInByteBuffer) : PlayS2CPacket {
    val entityId: Int = buffer.readVarInt()
    var entityUUID: UUID? = null
        private set
    val entity: Entity
    var velocity: Vec3d? = null
        private set

    init {
        if (buffer.versionId >= ProtocolVersions.V_15W31A) {
            entityUUID = buffer.readUUID()
        }
        val type: Int = if (buffer.versionId < ProtocolVersions.V_16W32A) {
            buffer.readByte().toInt()
        } else {
            buffer.readVarInt()
        }
        val position: Vec3d = buffer.readVec3d()
        val pitch = buffer.readAngle() // yaw/pitch is swapped
        val yaw = buffer.readAngle()
        val rotation = EntityRotation(yaw, pitch)
        if (buffer.versionId >= ProtocolVersions.V_22W14A) {
            val headYaw = buffer.readAngle()
        }
        val data = if (buffer.versionId >= ProtocolVersions.V_22W14A) buffer.readVarInt() else buffer.readInt()

        if (buffer.versionId >= ProtocolVersions.V_15W31A || data != 0) {
            velocity = buffer.readVelocity()
        }
        entity = if (buffer.versionId < ProtocolVersions.V_19W05A) {
            val entityResourceLocation = buffer.session.registries.entityObjectType[type].identifier
            buffer.session.registries.entityType[entityResourceLocation]!!.build(buffer.session, position, rotation, null, entityUUID, buffer.versionId)!! // ToDo: Entity data tweaking
        } else {
            buffer.session.registries.entityType[type].build(buffer.session, position, rotation, null, entityUUID, buffer.versionId)!!
        }
        entity.startInit()
        entity.setObjectData(data)
        velocity?.let { entity.physics.velocity = it }
    }

    override fun handle(session: PlaySession) {
        session.world.entities.add(entityId, entityUUID, entity)
    }

    override fun log(reducedLog: Boolean) {
        Log.log(LogMessageType.NETWORK_IN, level = LogLevels.VERBOSE) { "Entity object spawn (entityId=$entityId, entityUUID=$entityUUID, entity=$entity, velocity=$velocity)" }
    }
}
