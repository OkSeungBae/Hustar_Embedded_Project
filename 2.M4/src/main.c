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

#include "stm32f4xx_hal.h"		//관련레지스터의 주소 지정
#include "stm32f4xx_it.h"

GPIO_InitTypeDef gp, gp2;		//GPIO의 초기화를 위한 구조체형의 변수를 선언
GPIO_InitTypeDef LED;
UART_HandleTypeDef	UartHandle1;		//UART의 초기화를 위한 구조체형의 변수를 선언
UART_HandleTypeDef	UartHandle2;		//UART의 초기화를 위한 구조체형의 변수를 선언

//UART총신을 위한 정의
#define TxBufferSize (countof(TxBuffer)-1)	//송신 버퍼 사이즈를 정의
#define RxBufferSize 0xFF					//수신 버퍼 사이즈를 0xFF로 정의
#define countof(a) (sizeof(a) / sizeof(*(a)))		//데이터 사이즈

//UART 통신용 변수 선언
uint8_t TxBuffer[] = "Polling UART1\n\r";
uint8_t RxBuffer[RxBufferSize];

//UART의 초기설정을 위한 함수
void UART_config(void)
{
	//PA9 (UART1_Tx), PA10 (UART1_Rx)
	//UART의 클락을 활성화
	__HAL_RCC_GPIOA_CLK_ENABLE();
	__HAL_RCC_USART1_CLK_ENABLE();
	__HAL_RCC_USART2_CLK_ENABLE();

	//GPIO 설정
	gp.Pin = GPIO_PIN_9 | GPIO_PIN_10;
	gp.Mode = GPIO_MODE_AF_PP;
	gp.Pull = GPIO_NOPULL;
	gp.Speed = GPIO_SPEED_FREQ_VERY_HIGH;
	gp.Alternate = GPIO_AF7_USART1;
	HAL_GPIO_Init(GPIOA, &gp);

	//UART2 GPIO t섲어
	gp2.Pin = GPIO_PIN_2 | GPIO_PIN_3;
	gp2.Mode = GPIO_MODE_AF_PP;
	gp2.Pull = GPIO_NOPULL;
	gp2.Speed = GPIO_SPEED_FREQ_VERY_HIGH;
	gp2.Alternate = GPIO_AF7_USART2;
	HAL_GPIO_Init(GPIOA, &gp2);

	//UART의 동작 조건 설정
	UartHandle1.Instance = USART1;
	UartHandle1.Init.BaudRate = 9600;
	UartHandle1.Init.WordLength = UART_WORDLENGTH_8B;
	UartHandle1.Init.StopBits = UART_STOPBITS_1;
	UartHandle1.Init.Parity = UART_PARITY_NONE;
	UartHandle1.Init.HwFlowCtl = UART_HWCONTROL_NONE;
	UartHandle1.Init.Mode = UART_MODE_TX_RX;
	UartHandle1.Init.OverSampling = UART_OVERSAMPLING_16;

	//UART의 동작 조건 설정
	UartHandle2.Instance = USART2;
	UartHandle2.Init.BaudRate = 9600;
	UartHandle2.Init.WordLength = UART_WORDLENGTH_8B;
	UartHandle2.Init.StopBits = UART_STOPBITS_1;
	UartHandle2.Init.Parity = UART_PARITY_NONE;
	UartHandle2.Init.HwFlowCtl = UART_HWCONTROL_NONE;
	UartHandle2.Init.Mode = UART_MODE_TX_RX;
	UartHandle2.Init.OverSampling = UART_OVERSAMPLING_16;

	//UART 구성정보를 UartHandle1dp 설정된 값으로 초기화 함
	HAL_UART_Init(&UartHandle1);
	HAL_UART_Init(&UartHandle2);

	HAL_NVIC_SetPriority(USART1_IRQn, 0, 0);
	HAL_NVIC_EnableIRQ(USART1_IRQn);

	HAL_NVIC_SetPriority(USART2_IRQn, 0, 0);
	HAL_NVIC_EnableIRQ(USART2_IRQn);
}

//LED초기 configure함수
void LED_config(void)
{
	//Enable GPIOC Clock
	__HAL_RCC_GPIOC_CLK_ENABLE();

	//configure PC2 IO in output push-pull mode it drive external LED
	LED.Pin = GPIO_PIN_2;
	LED.Mode = GPIO_MODE_OUTPUT_PP;
	LED.Pull = GPIO_NOPULL;
	LED.Speed = GPIO_SPEED_LOW;
	HAL_GPIO_Init(GPIOC, &LED);

}

//지연 루틴
void ms_delay_int_count(volatile unsigned int nTime)
{
	nTime = (nTime * 14000);
	for(; nTime>0; nTime--);
}

int flag = 0;

//UART 인터럽트 Callback함수
void HAL_UART_RxCpltCallback(UART_HandleTypeDef* huart)
{
	if(huart->Instance == USART1){


	}
	if(huart->Instance == USART2){


	}


	//인터럽트 발생하면 PC로 보내는 함수
	HAL_UART_Transmit(&UartHandle1, (uint8_t*)RxBuffer, 1, 5);
}

int main(int argc, char* argv[])
{

	//Configure UART
	UART_config();
	LED_config();

	//TxBuffer에 저장되어 있는 내용을 PC로 보낸다
	HAL_UART_Transmit(&UartHandle1, (uint8_t*)TxBuffer, TxBufferSize, 0xFFFF);

	while (1)
	{
		HAL_UART_Receive_IT(&UartHandle1, (uint8_t*)RxBuffer, 1);
	}
}

#pragma GCC diagnostic pop

// ----------------------------------------------------------------------------
