package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

// DIFFERENTIAL OPERATORS

// FIRST DERIVATIVES

fun <T : DoubleOperators<T>> d_dx(F: IField<T>): IField<T> {
    return ((ShiftW(F) - ShiftE(F)) / (F.dx() * 2.0))
}

fun <T : DoubleOperators<T>> fd_dx(F: IField<T>): IField<T> {
    return ((ShiftW(F) - F) / F.dx())
}

fun <T : DoubleOperators<T>> bd_dx(F: IField<T>): IField<T> {
    return ((F - ShiftE(F)) / F.dx())
}

fun <T : DoubleOperators<T>> d_dy(F: IField<T>): IField<T> {
    return ((ShiftS(F) - ShiftN(F)) / (F.dy() * 2.0))
}

fun <T : DoubleOperators<T>> fd_dy(F: IField<T>): IField<T> {
    return ((ShiftS(F) - F) / F.dy())
}

fun <T : DoubleOperators<T>> bd_dy(F: IField<T>): IField<T> {
    return ((F - ShiftN(F)) / F.dy())
}


// SECOND DERIVATIVES

fun <T : DoubleOperators<T>> d2_dx2(F: IField<T>): IField<T> {
    return (ShiftW(F) - (F * 2.0) + ShiftE(F)) / (F.dx() * F.dx())
}

fun <T : DoubleOperators<T>> d2_dy2(F: IField<T>): IField<T> {
    return (ShiftN(F) - (F * 2.0) + ShiftS(F)) / (F.dy() * F.dy())
}

fun <T : DoubleOperators<T>> Laplacian(F: IField<T>): IField<T> {
    return (((ShiftW(F) + ShiftE(F)) * F.dy() * F.dy()) + ((ShiftS(F) + ShiftN(F)) * F.dx() * F.dx())) / (2.0 * (F.dx() * F.dx() + F.dy() * F.dy()))
}


// FOURTH DERIVATIVES

fun <T : DoubleOperators<T>> d4_dx4(F: IField<T>): IField<T> {
    return d2_dx2(d2_dx2(F))
}

fun <T : DoubleOperators<T>> d4_dy4(F: IField<T>): IField<T> {
    return d2_dy2(d2_dy2(F))
}

