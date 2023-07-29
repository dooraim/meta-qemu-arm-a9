KBRANCH_qemuarma9 ?= "v5.4/standard/arm-versatile-926ejs"
KMACHINE_qemuarma9 ?= "qemuarma9"
COMPATIBLE_MACHINE_qemuarma9 = "qemuarma9"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-vexpress-add-kgdb-and-trace.patch"
