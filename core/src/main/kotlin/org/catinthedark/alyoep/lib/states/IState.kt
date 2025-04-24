package org.catinthedark.alyoep.lib.states

interface IState {
    fun onActivate()
    fun onUpdate()
    fun onExit()
}
