KBRANCH_qemuarma9 ?= "v5.4/standard/arm-versatile-926ejs"
KMACHINE_qemuarma9 ?= "qemuarma9"
COMPATIBLE_MACHINE_qemuarma9 = "qemuarma9"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

#SRC_URI += "file://0001-update-for-vexpress-test.patch"

SRC_URI_append = " file://bsp.cfg"
KERNEL_FEATURES_append = " bsp.cfg"
