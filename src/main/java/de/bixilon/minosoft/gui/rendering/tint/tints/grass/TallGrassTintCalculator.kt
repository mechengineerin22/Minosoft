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

package de.bixilon.minosoft.gui.rendering.tint.tints.grass

import de.bixilon.minosoft.data.registries.biomes.Biome
import de.bixilon.minosoft.data.registries.blocks.state.BlockState
import de.bixilon.minosoft.data.world.positions.BlockPosition
import de.bixilon.minosoft.gui.rendering.tint.TintProvider

class TallGrassTintCalculator(val grassTintCalculator: GrassTintCalculator) : TintProvider {

    fun getColor(state: BlockState, biome: Biome?): Int {
        return grassTintCalculator.getBlockColor(biome) // TODO: check for top/bottom
    }

    override fun getBlockColor(state: BlockState, biome: Biome?, position: BlockPosition, tintIndex: Int): Int = getColor(state, biome)
    override fun getParticleColor(state: BlockState, biome: Biome?, position: BlockPosition) = getColor(state, biome)
}
