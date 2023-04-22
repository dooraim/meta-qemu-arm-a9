DESCRIPTION = "QEMU ARM A9 Debug Image"
LICENSE = "MIT"

require qemu-image-base.inc

COMPATIBLE_MACHINE = "qemuarma9"

IMAGE_INSTALL_append = " gdbserver"

EXTRA_IMAGE_FEATURES = "debug-tweaks tools-debug"
