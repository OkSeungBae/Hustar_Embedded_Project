#include "stm32f4xx_hal.h"
#include "stm32f4xx_it.h"

//UART 인터럽트 ISRㅡㄹ 위한 UartHandler변수를 외부정의 변수로 선언
extern UART_HandleTypeDef UartHandle1, UartHandle2;

void USART1_IRQHandler(void)
{
	HAL_UART_IRQHandler(&UartHandle1);		//UART인터럽트 callback함수
}
void USART2_IRQHandler(void)
{
	HAL_UART_IRQHandler(&UartHandle2);		//UART인터럽트 callback함수
}
