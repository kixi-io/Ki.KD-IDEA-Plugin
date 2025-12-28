package io.kixi.kd.intellij

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for KD file type registration.
 */
class KDFileTypeTest : FunSpec({

    context("KDFileType") {

        test("should have correct name") {
            KDFileType.name shouldBe "KD File"
        }

        test("should have correct default extension") {
            KDFileType.defaultExtension shouldBe "kd"
        }

        test("should have description") {
            KDFileType.description shouldBe "Ki Data file"
        }

        test("should have an icon") {
            KDFileType.icon shouldNotBe null
        }
    }

    context("KDLanguage") {

        test("should have correct ID") {
            KDLanguage.id shouldBe "KD"
        }

        test("should have correct display name") {
            KDLanguage.displayName shouldBe "Ki Data"
        }

        test("should be case sensitive") {
            KDLanguage.isCaseSensitive shouldBe true
        }
    }
})
