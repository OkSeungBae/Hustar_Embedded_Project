/*
 * This file is part of the 쨉OS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

// ----------------------------------------------------------------------------

#include <stdio.h>
#include <stdlib.h>
#include "diag/Trace.h"

#include "Timer.h"
#include "BlinkLed.h"

// ----------------------------------------------------------------------------
//
// Standalone STM32F4 led blink sample (trace via ITM).
//
// In debug configurations, demonstrate how to print a greeting message
// on the trace device. In release configurations the message is
// simply discarded.
//
// Then demonstrates how to blink a led with 1 Hz, using a
// continuous loop and SysTick delays.
//
// Trace support is enabled by adding the TRACE macro definition.
// By default the trace messages are forwarded to the ITM output,
// but can be rerouted to any device or completely suppressed, by
// changing the definitions required in system/src/diag/trace_impl.c
// (currently OS_USE_TRACE_ITM, OS_USE_TRACE_SEMIHOSTING_DEBUG/_STDOUT).
//

// ----- Timing definitions -------------------------------------------------

// Keep the LED on for 2/3 of a second.
#define BLINK_ON_TICKS  (TIMER_FREQUENCY_HZ * 3 / 4)
#define BLINK_OFF_TICKS (TIMER_FREQUENCY_HZ - BLINK_ON_TICKS)

// ----- main() ---------------------------------------------------------------

// Sample pragmas to cope with warnings. Please note the related line at
// the end of this function, used to pop the compiler diagnostics status.
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"
#pragma GCC diagnostic ignored "-Wmissing-declarations"
#pragma GCC diagnostic ignored "-Wreturn-type"

#include "stm32f4xx_hal.h"		//관련 레지스터의 주소 지정

GPIO_InitTypeDef LCD;		//GPIO의 초기화를 위한 구조체형의 변수 선언

//지연 루틴
static void ms_delay_int_count(volatile unsigned int nTime)	//ms지연
{
	nTime = (nTime * 14000);
	for(; nTime>0; nTime--);
}
static void us_delay_int_count(volatile unsigned int nTime)	//us지연
{
	nTime = (nTime * 12);
	for(; nTime>0; nTime--);
}

//CLCD의 초기성정용 함수의 선언
void CLCD_Config()
{
	//CLCD용 GPIO의 초기설정을 함
	__HAL_RCC_GPIOC_CLK_ENABLE();

	//CLCD R5(PC8), CLCD_E(PC9, DATA 4~5 (PC12 ~ 15)
	LCD.Pin = GPIO_PIN_8 | GPIO_PIN_9 | GPIO_PIN_12 | GPIO_PIN_13 | GPIO_PIN_14 | GPIO_PIN_15;
	LCD.Mode = GPIO_MODE_OUTPUT_PP;
	LCD.Pull = GPIO_NOPULL;
	LCD.Speed = GPIO_SPEED_FAST;
	HAL_GPIO_Init(GPIOC, &LCD);

	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_9, GPIO_PIN_RESET);		//CLCD_E = 0
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_8, GPIO_PIN_RESET);		//CLCD_RW = 0
}

void CLCD_Write(unsigned char rs, char data)
{
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_8, rs);				//CLDC_RS
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_9, GPIO_PIN_RESET);	//CLDC_E = 0
	us_delay_int_count(2);

	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_12, (data>>4) & 0x1);	//CLDC_DATA = Low_bit
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_13, (data>>5) & 0x1);
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_14, (data>>6) & 0x1);
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_15, (data>>7) & 0x1);

	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_9, GPIO_PIN_SET);	//CLCD_E = 1
	us_delay_int_count(2);
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_9, GPIO_PIN_RESET);	//CLCD_E = 0
	us_delay_int_count(2);

	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_12, (data>>0) & 0x1);	//CLCD_DATA = HIGH_bit
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_13, (data>>1) & 0x1);
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_14, (data>>2) & 0x1);
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_15, (data>>3) & 0x1);

	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_9, GPIO_PIN_SET);		//CLCD_E = 1
	us_delay_int_count(2);
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_9, GPIO_PIN_RESET);	//CLCD_E = 0
	ms_delay_int_count(2);
}

void CLCD_Init()
{
	HAL_GPIO_WritePin(GPIOC, GPIO_PIN_9, GPIO_PIN_RESET);		//CLCD_E = 0
	CLCD_Write(0, 0x33);		//4비트 설정 특수 명령
	CLCD_Write(0, 0x32);		//4비트 설정 특수 명령
	CLCD_Write(0, 0x28);		//_set_function
	CLCD_Write(0, 0x0F);		//_set_display
	CLCD_Write(0, 0x01);		//clcd_clear
	CLCD_Write(0, 0x06);		//_set_entry_mode
	CLCD_Write(0, 0x02);		//Return home
}

//메인 루틴
int main(int argc, char* argv[])
{
	//configure CLCD
	CLCD_Config();

	CLCD_Init();

	CLCD_Write(1, 'H');
	CLCD_Write(1, 'e');
	CLCD_Write(1, 'l');
	CLCD_Write(1, 'l');
	CLCD_Write(1, 'o');
	CLCD_Write(1, '!');
	CLCD_Write(1, '!');

	while (1);
}

#pragma GCC diagnostic pop

// ----------------------------------------------------------------------------
