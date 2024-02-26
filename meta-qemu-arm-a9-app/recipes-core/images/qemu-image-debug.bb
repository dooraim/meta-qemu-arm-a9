DESCRIPTION = "QEMU ARM A9 Debug Image"
LICENSE = "MIT"

require qemu-image-base.inc

COMPATIBLE_MACHINE = "qemuarma9"

IMAGE_INSTALL:append = "\
    gdbserver \
    gdb \
    strace \
    kernel-vmlinux \
    uftrace \
    lttng-tools \
    lttng-modules \
    ltrace \
    systemtap \
"

EXTRA_IMAGE_FEATURES:append = " dbg-pkgs debug-tweaks tools-debug tools-sdk tools-profile"
IMAGE_INSTALL:append = "kernel-vmlinux"

# Specifies to build packages with debugging information
DEBUG_BUILD = "1"

# Do not remove debug symbols
INHIBIT_PACKAGE_STRIP = "1"

# OPTIONAL: Do not split debug symbols in a separate file
INHIBIT_PACKAGE_DEBUG_SPLIT= "1"

PACKAGE_DEBUG_SPLIT_STYLE = "dbg-pkg"
PACKAGE_DEBUG_SPLIT = "1"

IMAGE_GEN_DEBUGFS = "1"
IMAGE_FSTYPES_DEBUGFS = "tar.bz2"

