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

package de.bixilon.minosoft.data.world.chunk.manager.size

import de.bixilon.kotlinglm.vec2.Vec2i
import de.bixilon.minosoft.data.world.World
import de.bixilon.minosoft.data.world.positions.ChunkPosition
import de.bixilon.minosoft.gui.rendering.util.vec.vec2.Vec2iUtil.EMPTY

class WorldSizeManager(private val world: World) {
    val size = WorldSize()


    fun onCreate(position: ChunkPosition) {
        val min = size.min
        val max = size.max
        if (!addExtreme(position, min, max)) return

        this.size.size = calculateSize(min, max)

        world.view.updateServerDistance()
    }

    fun onUnload(position: ChunkPosition) {
        val size = this.size
        if (position.x == size.min.x || position.z == size.min.y || position.x == size.max.x || position.z == size.max.y) {
            recalculate(false)
        }
    }

    fun clear() {
        size.clear()
    }

    private fun recalculate(lock: Boolean = true) {
        if (lock) world.lock.acquire()

        val min = Vec2i(Int.MAX_VALUE)
        val max = Vec2i(Int.MIN_VALUE)

        for ((position, _) in world.chunks.chunks.unsafe) {
            addExtreme(position, min, max)
        }
        val size = calculateSize(min, max)

        this.size.min = min
        this.size.max = max
        this.size.size = size
        if (lock) world.lock.release()

        world.view.updateServerDistance()
    }


    private fun addExtreme(position: ChunkPosition, min: Vec2i, max: Vec2i): Boolean {
        var changes = 0
        if (position.x < min.x) {
            min.x = position.x
            changes++
        }
        if (position.z < min.y) {
            min.y = position.z
            changes++
        }
        if (position.x > max.x) {
            max.x = position.x
            changes++
        }
        if (position.z > max.y) {
            max.y = position.z
            changes++
        }
        return changes > 0
    }

    private fun calculateSize(min: Vec2i, max: Vec2i, empty: Boolean = world.chunks.chunks.unsafe.isEmpty()): Vec2i {
        if (empty) return Vec2i.EMPTY
        val size = Vec2i((max - min) + 1)
        
        if (size.x > World.MAX_CHUNKS_SIZE) {
            size.x = World.MAX_CHUNKS_SIZE
        } else if (size.x < 0) {
            size.x = 0
        }

        if (size.y > World.MAX_CHUNKS_SIZE) {
            size.y = World.MAX_CHUNKS_SIZE
        } else if (size.y < 0) {
            size.y = 0
        }
        return size
    }
}
