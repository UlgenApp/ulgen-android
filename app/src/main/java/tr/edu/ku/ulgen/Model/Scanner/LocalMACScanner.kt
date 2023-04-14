package tr.edu.ku.ulgen.Model.Scanner


import java.net.Inet4Address
import java.net.NetworkInterface


object LocalMACScanner {

    private fun intToMacAddress(value: ByteArray) =
        MacAddress(value.joinToString(":") { String.format("%02X", it) })

    fun getMacAddresses(): Map<Inet4Address, MacAddress> {
        return try {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .flatMap { nic ->
                    nic.interfaceAddresses
                        .mapNotNull {
                            if (it.address is Inet4Address && nic.hardwareAddress != null) {
                                (it.address as Inet4Address) to intToMacAddress(nic.hardwareAddress)
                            } else {
                                null
                            }
                        }
                }
                .associate { it }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyMap()
        }
    }

    data class MacAddress(val address: String)
}
