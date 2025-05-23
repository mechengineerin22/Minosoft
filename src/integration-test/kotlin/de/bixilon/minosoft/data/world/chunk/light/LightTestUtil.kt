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

package de.bixilon.minosoft.data.world.chunk.light

import de.bixilon.minosoft.data.world.World
import de.bixilon.minosoft.data.world.chunk.chunk.Chunk
import de.bixilon.minosoft.data.world.chunk.light.types.LightLevel
import de.bixilon.minosoft.data.world.positions.BlockPosition
import de.bixilon.minosoft.data.world.positions.InChunkPosition
import org.testng.Assert

object LightTestUtil {

    fun Chunk.assertLight(x: Int, y: Int, z: Int, expected: Int) = assertLight(x, y, z, LightLevel(expected.toByte()))
    fun Chunk.assertLight(x: Int, y: Int, z: Int, expected: LightLevel) = assertLight(InChunkPosition(x, y, z), expected)
    fun Chunk.assertLight(position: InChunkPosition, expected: LightLevel) {
        val light = this.light[position]
        Assert.assertEquals(light.toString(), expected.toString())
    }

    fun World.assertLight(x: Int, y: Int, z: Int, expected: Int) = assertLight(x, y, z, LightLevel(expected.toByte()))
    fun World.assertLight(x: Int, y: Int, z: Int, expected: LightLevel) = assertLight(BlockPosition(x, y, z), expected)
    fun World.assertLight(position: BlockPosition, expected: LightLevel) {
        val light = this.getLight(position)
        Assert.assertEquals(light.toString(), expected.toString())
    }
}
